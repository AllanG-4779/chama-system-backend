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

@Table("contribution")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contribution {
    @Id
    private Long id;
    
    @Column("member_id")
    private Long memberId;
    
    @Column("chama_id")
    private Long chamaId;
    
    private BigDecimal amount;
    
    @Column("contribution_date")
    private LocalDate contributionDate;
    
    @Column("contribution_period")
    private String contributionPeriod; // YYYY-MM format
    
    @Column("payment_method")
    private String paymentMethod; // MPESA, CASH, BANK_TRANSFER
    
    @Column("payment_reference")
    private String paymentReference;
    
    @Column("external_payment_reference")
    private String externalPaymentReference;
    
    @Column("recorded_by")
    private String recordedBy;
    
    @Column("recorded_at")
    private LocalDateTime recordedAt;
}