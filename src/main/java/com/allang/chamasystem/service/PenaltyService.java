package com.allang.chamasystem.service;

import com.allang.chamasystem.models.Chama;
import com.allang.chamasystem.models.Invoice;
import com.allang.chamasystem.repository.ChamaRepository;
import com.allang.chamasystem.repository.ContributionConfigRepository;
import com.allang.chamasystem.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PenaltyService {

    private final ContributionConfigRepository contributionConfigRepository;
    private final InvoiceRepository invoiceRepository;
    private final ChamaRepository chamaRepository;
    private final InvoiceService invoiceService;

    /**
     * Scheduled job that runs daily to check for expired grace periods
     * and create penalty invoices for outstanding contributions
     */
    @Scheduled(cron = "0 0 1 * * *") // Run at 1 AM daily
    public void processExpiredGracePeriods() {
        LocalDate today = LocalDate.now();

        contributionConfigRepository.findPeriodsWithExpiredGracePeriod(today)
                .flatMap(period -> {
                    log.info("Processing expired grace period for period: {} (ended: {})",
                            period.getPeriod(), period.getGracePeriodEnd());

                    return chamaRepository.findById(period.getChamaId())
                            .flatMap(chama -> {
                                // Check if penalties are enabled
                                if (!"NONE".equals(chama.getLatePenaltyType())) {
                                    return createPenaltiesForPeriod(period.getId(), chama);
                                } else {
                                    log.info("Penalties disabled for chama: {}", chama.getName());
                                    return Mono.empty();
                                }
                            });
                })
                .doOnError(error -> log.error("Error processing expired grace periods: {}", error.getMessage(), error))
                .subscribe();
    }

    /**
     * Create penalty invoices for all outstanding contributions in a period
     */
    private Mono<Void> createPenaltiesForPeriod(Long periodId, Chama chama) {
        return invoiceRepository.findOutstandingInvoicesForPeriod(periodId)
                .flatMap(contributionInvoice -> createPenaltyInvoice(contributionInvoice, chama))
                .doOnNext(penalty -> log.info("Created penalty invoice {} for contribution invoice {}",
                        penalty.getId(), penalty.getPenaltyInvoiceId()))
                .then();
    }

    /**
     * Create a single penalty invoice for an outstanding contribution
     */
    public Mono<Invoice> createPenaltyInvoice(Invoice contributionInvoice, Chama chama) {
        return Mono.defer(() -> {
            // Calculate penalty amount based on type
            BigDecimal penaltyAmount = calculatePenaltyAmount(
                    contributionInvoice.getAmountOutstanding(),
                    chama.getLatePenaltyType(),
                    chama.getLatePenaltyAmount()
            );

            if (penaltyAmount.compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("Penalty amount is zero or negative for invoice: {}", contributionInvoice.getId());
                return Mono.empty();
            }

            // Create penalty invoice
            Invoice penaltyInvoice = Invoice.builder()
                    .memberId(contributionInvoice.getMemberId())
                    .chamaId(contributionInvoice.getChamaId())
                    .periodId(contributionInvoice.getPeriodId())
                    .amountDue(penaltyAmount)
                    .amountPaid(BigDecimal.ZERO)
                    .type("PENALTY")
                    .status("PENDING")
                    .issueDate(LocalDate.now())
                    .dueDate(LocalDate.now().plusDays(7)) // 7 days to pay penalty
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            return invoiceRepository.save(penaltyInvoice)
                    .flatMap(savedPenalty -> {
                        // Link contribution invoice to penalty invoice
                        contributionInvoice.setPenaltyInvoiceId(savedPenalty.getId());
                        return invoiceRepository.save(contributionInvoice)
                                .then(Mono.just(savedPenalty))
                                .doOnSuccess(pi -> {
                                    assert pi != null;
                                    log.info("Linked penalty invoice {} to contribution invoice {}",
                                            pi.getId(), contributionInvoice.getId());
                                });
                    })
                    .flatMap(savedPenalty -> {
                        // Auto-apply any available excess balance to the penalty invoice
                        log.info("Auto-applying excess to penalty invoice {}", savedPenalty.getId());
                        return invoiceService.autoApplyExcessToInvoice(
                                savedPenalty.getMemberId(),
                                savedPenalty.getId()
                        );
                    });
        });
    }

    /**
     * Calculate penalty amount based on type and outstanding balance
     */
    private BigDecimal calculatePenaltyAmount(BigDecimal outstandingAmount, String penaltyType, BigDecimal penaltyValue) {
        if (outstandingAmount == null || outstandingAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        if (penaltyValue == null || penaltyValue.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return switch (penaltyType) {
            case "FIXED" -> penaltyValue;
            case "PERCENTAGE" ->
                    outstandingAmount.multiply(penaltyValue).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
            default -> BigDecimal.ZERO;
        };
    }
}