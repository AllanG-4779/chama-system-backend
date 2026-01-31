package com.allang.chamasystem.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("invoices")
public class Invoice {
    @Id
    private Long id;
    private Long memberId;
    private Long chamaId;
    private BigDecimal amountDue;
    private BigDecimal amountPaid;

    @ReadOnlyProperty  // Database-computed: GREATEST(amount_paid - amount_due, 0)
    private BigDecimal excessBalance;

    @ReadOnlyProperty  // Database-computed: GREATEST(amount_due - amount_paid, 0)
    private BigDecimal amountOutstanding;

    private Long periodId;
    private LocalDate issueDate;
    private String type; // e.g., "CONTRIBUTION", "PENALTY", "OTHER"
    private LocalDate dueDate;
    private String status; // PENDING, PARTIAL, PAID, OVERPAID, OVERDUE
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
