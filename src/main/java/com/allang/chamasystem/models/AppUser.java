package com.allang.chamasystem.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@Table("app_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {
    @Id
    private Long id;
    
    @Column("member_id")
    private Long memberId;
    
    private String username;
    
    @Column("password_hash")
    private String passwordHash;
    
    private List<String> roles; // R2DBC maps PostgreSQL arrays to List
    
    private Boolean active;
    
    @Column("created_at")
    private LocalDateTime createdAt;
    
    @Column("updated_at")
    private LocalDateTime updatedAt;
}