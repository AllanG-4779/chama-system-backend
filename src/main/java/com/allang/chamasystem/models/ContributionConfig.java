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
@Table("contribution_config")
public class ContributionConfig {
    @Id
    private Long id;
    private Long chamaId;
    private String period;              // YYYY-MM
    private BigDecimal amount;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate gracePeriodEnd;
    private String frequency;           // MONTHLY, QUARTERLY, ANNUALLY
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
