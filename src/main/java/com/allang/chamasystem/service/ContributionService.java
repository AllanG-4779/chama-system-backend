package com.allang.chamasystem.service;

import com.allang.chamasystem.dto.ContributionDto;
import com.allang.chamasystem.exceptions.GenericExceptions;
import com.allang.chamasystem.models.Contribution;
import com.allang.chamasystem.models.ContributionConfig;
import com.allang.chamasystem.repository.ContributionConfigRepository;
import com.allang.chamasystem.repository.ContributionRepository;
import com.allang.chamasystem.repository.InvoiceRepository;
import com.allang.chamasystem.repository.LedgerEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ContributionService {

    private final ContributionRepository contributionRepository;
    private final TransactionalOperator transactionalOperator;
    private final InvoiceService invoiceService;
    private final LedgerService ledgerService;
    private final InvoiceRepository invoiceRepository;

    public Mono<Contribution> recordContributionPayment(ContributionDto contributionDto) {
        return invoiceRepository.findById(contributionDto.getInvoiceId())
                        .switchIfEmpty(Mono.error(new GenericExceptions("Please process invoice for the member before recording contribution payment.")))
                        .flatMap(invoice -> {
                            if (invoice.getAmountOutstanding().compareTo(BigDecimal.ZERO) <= 0) {
                                return Mono.error(new GenericExceptions("This invoice is already fully paid."));
                            }
                            if (!Objects.equals(invoice.getMemberId(), contributionDto.getMemberId())) {
                                return Mono.error(new GenericExceptions("Invoice does not belong to the specified member."));
                            }
                            var contribution = new Contribution();
                            contribution.setChamaId(contributionDto.getChamaId());
                            contribution.setMemberId(contributionDto.getMemberId());
                            contribution.setPeriodId(contributionDto.getPeriodId());
                            contribution.setAmount(contributionDto.getAmount());
                            contribution.setRecordedAt(LocalDateTime.now());
                            contribution.setPaymentMethod("MPESA");
                            contribution.setPaymentReference("MPESA12345");
                            contribution.setInvoiceId(invoice.getId());
                            contribution.setRecordedBy("system");
                            contribution.setContributionDate(LocalDate.now());
                            contribution.setUpdatedAt(java.time.LocalDateTime.now());

                            return transactionalOperator.execute(status ->
                                    contributionRepository.save(contribution)

                                            .then(invoiceService.updateInvoiceBalanceAndStatus(invoice.getId(), contributionDto.getAmount()))

                                            .then(ledgerService.createLedgerEntry(
                                                    contributionDto.getChamaId(),
                                                    contributionDto.getMemberId(),
                                                    "Contribution Payment for invoice " + invoice.getId(),
                                                    contributionDto.getAmount(),
                                                    true,
                                                    invoice.getId()
                                            ))
                                            .then(invoiceService.applyExcessToUnpaidInvoices(contributionDto.getMemberId()))
                            ).collectList().then(Mono.just(contribution)).as(transactionalOperator::transactional);
                        });
    }


}
