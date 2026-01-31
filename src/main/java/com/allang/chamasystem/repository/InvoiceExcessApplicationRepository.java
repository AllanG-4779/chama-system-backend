package com.allang.chamasystem.repository;

import com.allang.chamasystem.models.InvoiceExcessApplication;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Repository
public interface InvoiceExcessApplicationRepository extends ReactiveCrudRepository<InvoiceExcessApplication, Long> {

    // Find all applications from a source invoice
    Flux<InvoiceExcessApplication> findBySourceInvoiceId(Long sourceInvoiceId);

    // Find all applications to a target invoice
    Flux<InvoiceExcessApplication> findByTargetInvoiceId(Long targetInvoiceId);

    // Calculate total excess applied from a source invoice
    @Query("SELECT COALESCE(SUM(amount_applied), 0) FROM invoice_excess_applications WHERE source_invoice_id = :sourceInvoiceId")
    Mono<BigDecimal> getTotalAppliedFromSource(Long sourceInvoiceId);
}