package com.allang.chamasystem.repository;

import com.allang.chamasystem.models.Member;
import com.allang.chamasystem.models.TransactionAuditLog;
import org.jspecify.annotations.NonNull;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AuditLogRepository extends ReactiveCrudRepository<@NonNull TransactionAuditLog, @NonNull Long> {
}
