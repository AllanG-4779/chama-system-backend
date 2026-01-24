package com.allang.chamasystem.repository;

import com.allang.chamasystem.models.LedgerEntry;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface LedgerEntryRepository extends ReactiveCrudRepository<LedgerEntry, Long> {
    Mono<LedgerEntry> findTopByMemberIdAndChamaIdOrderByCreatedAtDesc(Long memberId, Long chamaId);
}
