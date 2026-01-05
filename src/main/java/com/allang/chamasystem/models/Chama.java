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

@Table("chama")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chama {
    @Id
    private Long id;

    private String name;

    @Column("registration_number")
    private String registrationNumber;

    @Column("monthly_contribution")
    private BigDecimal monthlyContribution;

    @Column("founded_date")
    private LocalDate foundedDate;

    @Column("meeting_schedule")
    private String meetingSchedule;

    private String status; // ACTIVE, SUSPENDED, DISSOLVED

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}