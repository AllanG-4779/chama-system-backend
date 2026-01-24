package com.allang.chamasystem.service;

import com.allang.chamasystem.models.Invoice;
import com.allang.chamasystem.repository.ChamaMemberRepository;
import com.allang.chamasystem.repository.ContributionConfigRepository;
import com.allang.chamasystem.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InvoiceService {
    private final ChamaMemberRepository chamaMemberRepository;
    private final ContributionConfigRepository contributionConfigRepository;
    private final InvoiceRepository invoiceRepository;

    public Mono<Invoice> createInvoiceForMember(Long chamaMemberId, Long periodId, String type) {
        return chamaMemberRepository.findById(chamaMemberId)
                .flatMap(chamaMember -> contributionConfigRepository.findById(periodId)
                        .flatMap(config -> {
                            // Logic to create invoice based on type and config
                            var invoice = new Invoice();
                            invoice.setAmountDue(config.getAmount());
                            invoice.setMemberId(chamaMember.getMemberId());
                            invoice.setDueDate(config.getGracePeriodEnd());
                            invoice.setPeriodId(periodId);
                            invoice.setType(type);
                            invoice.setStatus("PENDING");
                            invoice.setChamaId(config.getChamaId());
                            invoice.setCreatedAt(LocalDateTime.now());
                            invoice.setIssueDate(LocalDate.now());
                            invoice.setUpdatedAt(LocalDateTime.now());
                            return invoiceRepository.save(invoice);
                        }));
    }

    public Mono<Void> updateInvoiceBalanceAndStatus(Long invoiceId, java.math.BigDecimal amountPaid) {
        return invoiceRepository.findById(invoiceId)
                .flatMap(invoice -> {
                    var newAmountDue = invoice.getAmountDue().subtract(amountPaid);
                    invoice.setAmountDue(newAmountDue);
                    if (newAmountDue.compareTo(java.math.BigDecimal.ZERO) <= 0) {
                        invoice.setStatus("PAID");
                    } else {
                        invoice.setStatus("PARTIAL");
                    }
                    invoice.setUpdatedAt(LocalDateTime.now());
                    return invoiceRepository.save(invoice);
                }).then();
    }

    public Mono<Void> autoCreateInvoicesForContributions(Long chamaId, Long periodId) {
        return chamaMemberRepository.findAllByChamaId(chamaId)
                .flatMap(each -> createInvoiceForMember(each.getId(), periodId, "CONTRIBUTION"))
                .then();
    }

    public Mono<Void> autoCreateInvoicesForMember(Long memberId, Long chamaId, Long periodId) {
        return chamaMemberRepository.findByChamaIdAndMemberId(chamaId, memberId)
                .flatMap(chamaMember -> createInvoiceForMember(chamaMember.getId(), periodId, "CONTRIBUTION"))
                .then();
    }


}
