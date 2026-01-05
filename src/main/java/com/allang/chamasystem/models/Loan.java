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

@Table("loan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Loan {
    @Id
    private Long id;
    
    @Column("member_id")
    private Long memberId;
    
    @Column("chama_id")
    private Long chamaId;
    
    @Column("principal_amount")
    private BigDecimal principalAmount;
    
    @Column("interest_rate")
    private BigDecimal interestRate;
    
    @Column("repayment_months")
    private Integer repaymentMonths;
    
    @Column("total_amount")
    private BigDecimal totalAmount;
    
    @Column("monthly_installment")
    private BigDecimal monthlyInstallment;
    
    @Column("application_date")
    private LocalDate applicationDate;
    
    @Column("approval_date")
    private LocalDate approvalDate;
    
    @Column("disbursement_date")
    private LocalDate disbursementDate;
    
    @Column("expected_completion_date")
    private LocalDate expectedCompletionDate;
    
    private String status; // PENDING, APPROVED, REJECTED, DISBURSED, COMPLETED, DEFAULTED
    
    @Column("approved_by")
    private String approvedBy;
    
    @Column("rejection_reason")
    private String rejectionReason;
    
    @Column("created_at")
    private LocalDateTime createdAt;
    
    @Column("updated_at")
    private LocalDateTime updatedAt;
}