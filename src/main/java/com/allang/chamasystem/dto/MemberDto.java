package com.allang.chamasystem.dto;

import java.time.LocalDate;


public record MemberDto(Long chamaId, Long id, String firstName, String lastName, String email, String phoneNumber,
                        LocalDate dateOfBirth, String idNumber) {
}
