package com.allang.chamasystem.repository;

import com.allang.chamasystem.models.AppUser;
import org.jspecify.annotations.NonNull;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AppUserRepository extends ReactiveCrudRepository<@NonNull AppUser, @NonNull Long> {
}
