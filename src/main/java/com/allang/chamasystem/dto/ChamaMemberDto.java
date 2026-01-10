package com.allang.chamasystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChamaMemberDto {
    Long memberId;
    Long chamaId;
    String role;
}
