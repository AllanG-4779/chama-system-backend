package com.allang.chamasystem.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table("member")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    private Long id;
    
    @Column("chama_id")
    private Long chamaId;
    
    @Column("full_name")
    private String fullName;
    
    @Column("phone_number")
    private String phoneNumber;
    
    private String email;
    
    @Column("id_number")
    private String idNumber;
    
    @Column("joined_date")
    private LocalDate joinedDate;
    
    private String role; // CHAIRMAN, TREASURER, SECRETARY, MEMBER
    
    private String status; // ACTIVE, INACTIVE, SUSPENDED
    
    @Column("created_at")
    private LocalDateTime createdAt;
    
    @Column("updated_at")
    private LocalDateTime updatedAt;
}