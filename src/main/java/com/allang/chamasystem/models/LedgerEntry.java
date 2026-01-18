package com.allang.chamasystem.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("ledger_entries")
public class LedgerEntry {
    @Id
    private Long id;
    private Long memberId;
    private Long chamaId;
    private Long invoiceId;
    private LocalDate entryDate;
    private String description;
    private BigDecimal debitAmount;
    private BigDecimal creditAmount;
    private BigDecimal balanceAfter;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
