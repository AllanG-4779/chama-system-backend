package com.allang.chamasystem.repository;

import com.allang.chamasystem.models.Chama;
import com.allang.chamasystem.models.Contribution;
import org.jspecify.annotations.NonNull;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ContributionRepository extends ReactiveCrudRepository<@NonNull Contribution, @NonNull Long> {
}
