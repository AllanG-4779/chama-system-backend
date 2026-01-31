package com.allang.chamasystem.repository;

import com.allang.chamasystem.models.Invoice;
import com.allang.chamasystem.models.LedgerEntry;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InvoiceRepository extends ReactiveCrudRepository<Invoice, Long> {
    Mono<Invoice> findByMemberIdAndChamaId(Long chamaMemberId, Long periodId);

    Mono<Invoice> findByPeriodIdAndChamaIdAndMemberId(Long periodId, Long chamaId, Long memberId);

    // Find invoices with available excess balance for a member
    @Query("SELECT * FROM invoices WHERE member_id = :memberId AND excess_balance > 0 ORDER BY created_at ASC")
    Flux<Invoice> findInvoicesWithExcessByMemberId(Long memberId);

    // Find all invoices for a member
    Flux<Invoice> findByMemberId(Long memberId);

    // Find contribution invoices with outstanding balance that don't have penalties yet
    @Query("SELECT * FROM invoices WHERE period_id = :periodId AND type = 'CONTRIBUTION' AND amount_outstanding > 0 AND penalty_invoice_id IS NULL")
    Flux<Invoice> findOutstandingInvoicesForPeriod(Long periodId);
}
