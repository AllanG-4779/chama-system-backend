package com.allang.chamasystem.repository;

import com.allang.chamasystem.models.Chama;
import org.jspecify.annotations.NonNull;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ChamaRepository extends ReactiveCrudRepository<@NonNull Chama, @NonNull Long> {
}
