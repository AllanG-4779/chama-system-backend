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
@Table("invoices")
public class Invoice {
    @Id
    private Long id;
    private Long memberId;
    private Long chamaId;
    private BigDecimal amountDue;
    private Long periodId;
    private LocalDate issueDate;
    private String type; // e.g., "CONTRIBUTION", "PENALTY", "OTHER"
    private LocalDate dueDate;
    private String status; // PENDING, PAID, OVERDUE
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
