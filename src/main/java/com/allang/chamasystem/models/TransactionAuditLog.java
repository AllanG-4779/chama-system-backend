package com.allang.chamasystem.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("transaction_audit_log")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionAuditLog {
    @Id
    private Long id;
    
    @Column("transaction_type")
    private String transactionType;
    
    @Column("transaction_id")
    private Long transactionId;
    
    @Column("reference_number")
    private String referenceNumber;
    
    private BigDecimal amount;
    
    private String currency;
    
    @Column("member_id")
    private Long memberId;
    
    @Column("chama_id")
    private Long chamaId;
    
    private String action;
    
    @Column("status_before")
    private String statusBefore;
    
    @Column("status_after")
    private String statusAfter;
    
    @Column("performed_by_id")
    private Long performedById;
    
    @Column("performed_by_name")
    private String performedByName;
    
    @Column("performed_by_role")
    private String performedByRole;
    
    private String notes;
    
    private String metadata; // JSON string
    
    @Column("ip_address")
    private String ipAddress;
    
    @Column("user_agent")
    private String userAgent;
    
    private LocalDateTime timestamp;
}