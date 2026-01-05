package com.allang.chamasystem.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table("loan_repayment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanRepayment {
    @Id
    private Long id;

    @Column("loan_id")
    private Long loanId;

    private BigDecimal amount;

    @Column("payment_date")
    private LocalDate paymentDate;

    @Column("repayment_period")
    private String repaymentPeriod; // YYYY-MM format

    @Column("payment_method")
    private String paymentMethod;

    @Column("payment_reference")
    private String paymentReference;

    @Column("external_payment_reference")
    private String externalPaymentReference;

    @Column("recorded_by")
    private String recordedBy;

    @Column("recorded_at")
    private LocalDateTime recordedAt;
}