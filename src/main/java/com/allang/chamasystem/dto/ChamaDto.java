package com.allang.chamasystem.dto;

import java.math.BigDecimal;

public record ChamaDto(
        Long id,
        String name,
        String description,
        BigDecimal contributionAmount,
        String contributionSchedule,
        String registrationNumber
) {
}
