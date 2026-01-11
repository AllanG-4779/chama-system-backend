package com.allang.chamasystem.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class AppUserDto {
    private String username;
    private String password;
    private String idNumber;
    private Long memberId;
}
