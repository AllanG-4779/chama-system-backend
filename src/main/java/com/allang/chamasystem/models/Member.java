package com.allang.chamasystem.models;

import com.allang.chamasystem.dto.MemberDto;
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
    @Column("first_name")
    private String firstName;
    @Column("last_name")
    private String lastName;
    @Column("date_of_birth")
    private LocalDate dateOfBirth;
    @Column("phone_number")
    private String phoneNumber;
    private String email;
    @Column("id_number")
    private String idNumber;
    private String status; // ACTIVE, INACTIVE, SUSPENDED
    @Column("created_at")
    private LocalDateTime createdAt;
    @Column("updated_at")
    private LocalDateTime updatedAt;
}