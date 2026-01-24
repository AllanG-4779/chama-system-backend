package com.allang.chamasystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LedgerEntryDto {
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
