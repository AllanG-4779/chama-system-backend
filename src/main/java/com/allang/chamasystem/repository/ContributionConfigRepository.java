package com.allang.chamasystem.repository;

import com.allang.chamasystem.models.ContributionConfig;
import com.allang.chamasystem.models.Invoice;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ContributionConfigRepository extends ReactiveCrudRepository<ContributionConfig, Long> {
    Mono<Long> countAllByChamaId(Long chamaId);
}
