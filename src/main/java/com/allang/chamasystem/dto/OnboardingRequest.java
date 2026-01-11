package com.allang.chamasystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingRequest {
    private MemberDto memberInfo;
    private AppUserDto credentials;
    private Long chamaId;
    private String role;
}
