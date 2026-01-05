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

@Table("penalty")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Penalty {
    @Id
    private Long id;
    
    @Column("member_id")
    private Long memberId;
    
    @Column("chama_id")
    private Long chamaId;
    
    private String type; // LATE_CONTRIBUTION, MISSED_MEETING, LOAN_DEFAULT, OTHER
    
    private BigDecimal amount;
    
    @Column("incurred_date")
    private LocalDate incurredDate;
    
    private String reason;
    
    private Boolean paid;
    
    @Column("paid_date")
    private LocalDate paidDate;
    
    @Column("recorded_by")
    private String recordedBy;
    
    @Column("created_at")
    private LocalDateTime createdAt;
}