package com.allang.chamasystem.events;

public record ContributionPeriodCreatedEvent(
        Long chamaId,
        Long periodId, boolean singleMember, Long memberId) {
}
