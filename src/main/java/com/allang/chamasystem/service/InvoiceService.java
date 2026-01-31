package com.allang.chamasystem.service;

import com.allang.chamasystem.exceptions.GenericExceptions;
import com.allang.chamasystem.models.Invoice;
import com.allang.chamasystem.models.InvoiceExcessApplication;
import com.allang.chamasystem.repository.ChamaMemberRepository;
import com.allang.chamasystem.repository.ContributionConfigRepository;
import com.allang.chamasystem.repository.InvoiceExcessApplicationRepository;
import com.allang.chamasystem.repository.InvoiceRepository;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {
    private final ChamaMemberRepository chamaMemberRepository;
    private final ContributionConfigRepository contributionConfigRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceExcessApplicationRepository excessApplicationRepository;

    public Mono<Invoice> createInvoiceForMember(Long chamaMemberId, Long periodId, String type) {
        return contributionConfigRepository.findById(periodId)
                .flatMap(config -> {
                    // Logic to create invoice based on type and config
                    var invoice = new Invoice();
                    invoice.setAmountDue(config.getAmount());
                    invoice.setAmountPaid(java.math.BigDecimal.ZERO);
                    invoice.setMemberId(chamaMemberId);
                    invoice.setDueDate(config.getGracePeriodEnd());
                    invoice.setPeriodId(periodId);
                    invoice.setType(type);
                    invoice.setStatus("PENDING");
                    invoice.setChamaId(config.getChamaId());
                    invoice.setCreatedAt(LocalDateTime.now());
                    invoice.setIssueDate(LocalDate.now());
                    invoice.setUpdatedAt(LocalDateTime.now());
                    return invoiceRepository.save(invoice);
                });
    }

    public Mono<Void> updateInvoiceBalanceAndStatus(Long invoiceId, java.math.BigDecimal paymentAmount) {
        return invoiceRepository.findById(invoiceId)
                .flatMap(invoice -> {
                    // Add payment to the total amount paid
                    var currentAmountPaid = invoice.getAmountPaid() != null
                            ? invoice.getAmountPaid()
                            : java.math.BigDecimal.ZERO;
                    var newAmountPaid = currentAmountPaid.add(paymentAmount);
                    invoice.setAmountPaid(newAmountPaid);

                    // Calculate remaining balance (amountDue - amountPaid)
                    var remainingBalance = invoice.getAmountDue().subtract(newAmountPaid);

                    // Update status based on remaining balance
                    if (remainingBalance.compareTo(java.math.BigDecimal.ZERO) < 0) {
                        // Overpaid: excess_balance will be positive (amount_paid - amount_due)
                        invoice.setStatus("OVERPAID");
                    } else if (remainingBalance.compareTo(java.math.BigDecimal.ZERO) == 0) {
                        // Fully paid
                        invoice.setStatus("PAID");
                    } else if (newAmountPaid.compareTo(java.math.BigDecimal.ZERO) > 0) {
                        // Partially paid
                        invoice.setStatus("PARTIAL");
                    } else {
                        // No payment made yet
                        invoice.setStatus("PENDING");
                    }

                    invoice.setUpdatedAt(LocalDateTime.now());
                    return invoiceRepository.save(invoice);
                }).then();
    }

    public Mono<Void> autoCreateInvoicesForContributions(Long chamaId, Long periodId) {
        return chamaMemberRepository.findAllByChamaId(chamaId)
                .flatMap(each -> createInvoiceAndAutoApplyExcess(each.getId(), periodId, "CONTRIBUTION"))
                .then();
    }

    public Mono<Void> autoCreateInvoicesForMember(Long memberId, Long periodId) {
        return createInvoiceAndAutoApplyExcess(memberId, periodId, "CONTRIBUTION")
                .then();
    }

    /**
     * Apply excess balance from one invoice to another
     */
    public Mono<InvoiceExcessApplication> applyExcessToInvoice(Long sourceInvoiceId, Long targetInvoiceId, BigDecimal amountToApply) {
        return invoiceRepository.findById(sourceInvoiceId)
                .zipWith(invoiceRepository.findById(targetInvoiceId))
                .flatMap(tuple -> {
                    Invoice sourceInvoice = tuple.getT1();
                    Invoice targetInvoice = tuple.getT2();

                    // Get available excess from source invoice
                    return getAvailableExcessForInvoice(sourceInvoiceId)
                            .flatMap(availableExcess -> {
                                if (availableExcess.compareTo(BigDecimal.ZERO) <= 0) {
                                    return Mono.error(new GenericExceptions("Source invoice has no available excess balance"));
                                }

                                if (amountToApply.compareTo(availableExcess) > 0) {
                                    return Mono.error(new GenericExceptions("Amount to apply exceeds available excess"));
                                }

                                // Apply the excess as payment to target invoice
                                return updateInvoiceBalanceAndStatus(targetInvoiceId, amountToApply)
                                        .then(Mono.defer(() -> {
                                            // Record the application
                                            var application = InvoiceExcessApplication.builder()
                                                    .sourceInvoiceId(sourceInvoiceId)
                                                    .targetInvoiceId(targetInvoiceId)
                                                    .amountApplied(amountToApply)
                                                    .appliedAt(LocalDateTime.now())
                                                    .build();

                                            return excessApplicationRepository.save(application)
                                                    .doOnSuccess(saved -> log.info(
                                                            "Applied {} excess from invoice {} to invoice {}",
                                                            amountToApply, sourceInvoiceId, targetInvoiceId
                                                    ));
                                        }));
                            });
                });
    }

    /**
     * Get available excess for a specific invoice (excess_balance - already applied)
     */
    public Mono<BigDecimal> getAvailableExcessForInvoice(Long invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .flatMap(invoice -> {
                    BigDecimal excessBalance = invoice.getExcessBalance() != null
                            ? invoice.getExcessBalance()
                            : BigDecimal.ZERO;

                    // Get total amount already applied from this invoice
                    return excessApplicationRepository.getTotalAppliedFromSource(invoiceId)
                            .map(excessBalance::subtract)
                            .defaultIfEmpty(excessBalance);
                });
    }

    /**
     * Auto-apply available excess balance to any invoice (reusable for contributions, penalties, etc.)
     * @param memberId The member whose excess to apply
     * @param targetInvoiceId The invoice to apply excess to
     * @return Updated invoice with excess applied
     */
    public Mono<Invoice> autoApplyExcessToInvoice(Long memberId, Long targetInvoiceId) {
        return invoiceRepository.findById(targetInvoiceId)
                .flatMap(targetInvoice -> {
                    // Find invoices with available excess for this member
                    return invoiceRepository.findInvoicesWithExcessByMemberId(memberId)
                            .flatMap(sourceInvoice -> getAvailableExcessForInvoice(sourceInvoice.getId())
                                    .flatMap(availableExcess -> {
                                        if (availableExcess.compareTo(BigDecimal.ZERO) <= 0) {
                                            return Mono.empty();
                                        }

                                        // Calculate how much to apply (min of available excess and outstanding amount)
                                        return invoiceRepository.findById(targetInvoiceId)
                                                .flatMap(freshInvoice -> {
                                                    BigDecimal outstanding = freshInvoice.getAmountOutstanding() != null
                                                            ? freshInvoice.getAmountOutstanding()
                                                            : freshInvoice.getAmountDue();

                                                    if (outstanding.compareTo(BigDecimal.ZERO) <= 0) {
                                                        return Mono.empty(); // Already fully paid
                                                    }

                                                    BigDecimal amountToApply = availableExcess.min(outstanding);

                                                    return applyExcessToInvoice(
                                                            sourceInvoice.getId(),
                                                            targetInvoiceId,
                                                            amountToApply
                                                    );
                                                });
                                    })
                            )
                            .then(invoiceRepository.findById(targetInvoiceId)) // Return the updated invoice
                            .defaultIfEmpty(targetInvoice); // If no excess to apply, return original
                });
    }

    /**
     * Auto-apply available excess when creating a new invoice for a member
     */
    public Mono<Invoice> createInvoiceAndAutoApplyExcess(Long chamaMemberId, Long periodId, String type) {
        return createInvoiceForMember(chamaMemberId, periodId, type)
                .flatMap(newInvoice -> autoApplyExcessToInvoice(chamaMemberId, newInvoice.getId()));
    }

}
