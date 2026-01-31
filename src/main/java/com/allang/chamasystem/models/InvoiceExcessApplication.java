package com.allang.chamasystem.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("invoice_excess_applications")
public class InvoiceExcessApplication {
    @Id
    private Long id;
    private Long sourceInvoiceId;  // Invoice that had the excess
    private Long targetInvoiceId;  // Invoice where excess was applied
    private BigDecimal amountApplied;
    private LocalDateTime appliedAt;
}