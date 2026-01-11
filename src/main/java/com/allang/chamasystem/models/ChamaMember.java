package com.allang.chamasystem.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Table("chama_member")
public class ChamaMember {
    @Id
    private Long id;
    private Long chamaId;
    private Long memberId;
    private String role; // ADMIN, MEMBER, TREASURER, SECRETARY
    private String status;
    private LocalDateTime joinedAt;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
}
