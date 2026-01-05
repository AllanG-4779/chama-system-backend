package com.allang.chamasystem.repository;

import com.allang.chamasystem.models.AppUser;
import com.allang.chamasystem.models.Member;
import org.jspecify.annotations.NonNull;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface MemberRepository extends ReactiveCrudRepository<@NonNull Member, @NonNull Long> {
}
