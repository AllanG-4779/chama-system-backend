package com.allang.chamasystem.repository;

import com.allang.chamasystem.models.ContributionConfig;
import com.allang.chamasystem.models.Invoice;
import io.micrometer.observation.ObservationFilter;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface ContributionConfigRepository extends ReactiveCrudRepository<ContributionConfig, Long> {
    Mono<Long> countAllByChamaId(Long chamaId);
    Mono<ContributionConfig> findByChamaIdOrderByEndDateDesc(Long chamaId);

    Mono<ContributionConfig> findFirstByChamaIdOrderByEndDateDesc(Long id);

    Mono<ContributionConfig> findByChamaIdAndStartDate(Long chamaId, LocalDate today);
}
