package com.allang.chamasystem.repository;

import com.allang.chamasystem.models.Chama;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface ChamaRepository extends ReactiveCrudRepository<Chama, Long> {
    Mono<Boolean> existsByRegistrationNumber(String registrationNumber);

    Flux<Chama> findAllBy(Pageable pageable);

    Mono<Optional<Chama>> findByRegistrationNumber(String registrationNumber);

}
