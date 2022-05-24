package com.accenture.techtask;

import com.accenture.techtask.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends
        JpaRepository<Transaction, Long> {
}
