package com.allang.chamasystem.repository;

import com.allang.chamasystem.models.Invoice;
import com.allang.chamasystem.models.LedgerEntry;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface InvoiceRepository extends ReactiveCrudRepository<Invoice, Long> {
}
