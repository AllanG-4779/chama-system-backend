package com.allang.chamasystem.repository;

import com.allang.chamasystem.dto.MemberDto;
import com.allang.chamasystem.models.AppUser;
import com.allang.chamasystem.models.Member;
import org.jspecify.annotations.NonNull;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface MemberRepository extends ReactiveCrudRepository<Member, Long> {
    Mono<Boolean> existsByIdNumber(String idNumber);

    Mono<Boolean> existsByEmail(String email);

    Mono<Boolean> existsByPhoneNumber(String phoneNumber);
}
