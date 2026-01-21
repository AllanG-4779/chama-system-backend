package com.allang.chamasystem.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ChamaDto(
        Long id,
        String name,
        String description,
        BigDecimal contributionAmount,
        String contributionSchedule,
        String registrationNumber,
        MemberDto contactPerson,
        LocalDate anchorageDate
) {
}
