package com.allang.chamasystem.repository;

import com.allang.chamasystem.models.LoanRepayment;
import org.jspecify.annotations.NonNull;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface LoanRepaymentRepository extends ReactiveCrudRepository<@NonNull LoanRepayment, @NonNull Long> {
}
