package com.allang.chamasystem.service;

import com.allang.chamasystem.models.LedgerEntry;
import com.allang.chamasystem.repository.LedgerEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class LedgerService {
    private final LedgerEntryRepository ledgerEntryRepository;

    public Mono<Void> createLedgerEntry(Long chamaId, Long memberId, String description,
                                        java.math.BigDecimal amount, boolean isCredit, Long invoiceId) {
        return ledgerEntryRepository.findTopByMemberIdAndChamaIdOrderByCreatedAtDesc(memberId, chamaId)
                .switchIfEmpty(Mono.just(new LedgerEntry()))
                .flatMap(existingEntry -> {
                    var prevAmount = existingEntry.getBalanceAfter() != null ? existingEntry.getBalanceAfter() : BigDecimal.ZERO;
                    var newLedgerEntry = createLedgerEntity(chamaId, memberId, description, amount, isCredit, invoiceId, prevAmount);
                    return ledgerEntryRepository.save(newLedgerEntry);
                }).then();

    }

    private LedgerEntry createLedgerEntity(Long chamaId, Long memberId,
                                           String description,
                                           java.math.BigDecimal amount, boolean isCredit, Long invoiceId, BigDecimal prevAmount) {
        var ledgerEntry = new com.allang.chamasystem.models.LedgerEntry();
        ledgerEntry.setChamaId(chamaId);
        ledgerEntry.setMemberId(memberId);
        ledgerEntry.setDescription(description);
        ledgerEntry.setEntryDate(LocalDate.now());
        if (isCredit) {
            ledgerEntry.setCreditAmount(amount);
            ledgerEntry.setDebitAmount(java.math.BigDecimal.ZERO);
            ledgerEntry.setBalanceAfter(prevAmount.add(amount));
        } else {
            ledgerEntry.setDebitAmount(amount);
            ledgerEntry.setCreditAmount(java.math.BigDecimal.ZERO);
            ledgerEntry.setBalanceAfter(prevAmount.subtract(amount));
        }
        ledgerEntry.setCreatedAt(java.time.LocalDateTime.now());
        ledgerEntry.setUpdatedAt(java.time.LocalDateTime.now());
        ledgerEntry.setInvoiceId(invoiceId);
        return ledgerEntry;
    }
}
