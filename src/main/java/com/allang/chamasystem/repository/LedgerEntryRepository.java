package com.allang.chamasystem.repository;

import com.allang.chamasystem.models.LedgerEntry;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface LedgerEntryRepository extends ReactiveCrudRepository<LedgerEntry, Long> {
}
