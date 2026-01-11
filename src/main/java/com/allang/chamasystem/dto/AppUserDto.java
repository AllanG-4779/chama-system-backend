package com.allang.chamasystem.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppUserDto {
    private String username;
    private String password;
    private String idNumber;
    private Long memberId;
    private List<String> roles;
}
