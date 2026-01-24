package com.allang.chamasystem.repository;

import com.allang.chamasystem.models.Invoice;
import com.allang.chamasystem.models.LedgerEntry;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface InvoiceRepository extends ReactiveCrudRepository<Invoice, Long> {
    Mono<Invoice> findByMemberIdAndChamaId(Long chamaMemberId, Long periodId);

    Mono<Invoice> findByPeriodIdAndChamaIdAndMemberId(Long periodId, Long chamaId, Long memberId);
}
