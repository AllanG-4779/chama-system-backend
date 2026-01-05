package com.allang.chamasystem.repository;

import com.allang.chamasystem.models.Contribution;
import com.allang.chamasystem.models.Loan;
import org.jspecify.annotations.NonNull;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface LoanRepository extends ReactiveCrudRepository<@NonNull Loan, @NonNull Long> {
}
