package com.allang.chamasystem.repository;

import com.allang.chamasystem.dto.AppUserDto;
import com.allang.chamasystem.models.AppUser;
import org.jspecify.annotations.NonNull;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface AppUserRepository extends ReactiveCrudRepository<@NonNull AppUser, @NonNull Long> {
    Mono<Boolean> existsByUsername(String username);

    Mono<Boolean> existsByMemberId(Long memberId);
}
