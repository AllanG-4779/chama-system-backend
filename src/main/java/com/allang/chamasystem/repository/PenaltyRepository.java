package com.allang.chamasystem.repository;

import com.allang.chamasystem.models.Member;
import com.allang.chamasystem.models.Penalty;
import org.jspecify.annotations.NonNull;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PenaltyRepository extends ReactiveCrudRepository<@NonNull Penalty, @NonNull Long> {
}
