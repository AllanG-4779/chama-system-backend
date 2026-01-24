package com.allang.chamasystem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ContributionDto {
    @NotNull
    private Long memberId;
    @NotNull
    private Long chamaId;
    @NotNull
    private BigDecimal amount;
    private LocalDate contributionDate;
    @NotNull
    private Long periodId; // YYYY-MM
    private String paymentMethod;      // MPESA, CASH, BANK_TRANSFER
    private String paymentReference;
    private String externalPaymentReference;
    private String recordedBy;
    private LocalDateTime recordedAt;
}
