package com.allang.chamasystem.repository;

import com.allang.chamasystem.models.ContributionConfig;
import com.allang.chamasystem.models.Invoice;
import io.micrometer.observation.ObservationFilter;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface ContributionConfigRepository extends ReactiveCrudRepository<ContributionConfig, Long> {
    Mono<Long> countAllByChamaId(Long chamaId);
    Mono<ContributionConfig> findByChamaIdOrderByEndDateDesc(Long chamaId);

    Mono<ContributionConfig> findFirstByChamaIdOrderByEndDateDesc(Long id);
    Flux<ContributionConfig> findAllByChamaId(Long chamaId);

    Mono<ContributionConfig> findByChamaIdAndStartDate(Long chamaId, LocalDate today);

    // Find periods where grace period has expired (yesterday or earlier)
    @Query("SELECT * FROM contribution_config WHERE grace_period_end < :today")
    Flux<ContributionConfig> findPeriodsWithExpiredGracePeriod(LocalDate today);

    Mono<ContributionConfig> findByChamaIdAndPeriod(Long chamaId, String period);
}
