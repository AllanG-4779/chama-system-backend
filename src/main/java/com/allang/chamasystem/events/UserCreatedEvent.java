package com.allang.chamasystem.events;

import java.time.Instant;

public record UserCreatedEvent(
        String userId,
        Instant createdAt
) {
}
