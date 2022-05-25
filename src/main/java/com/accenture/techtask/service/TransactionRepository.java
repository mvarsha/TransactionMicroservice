package com.accenture.techtask.service;

import com.accenture.techtask.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends
        JpaRepository<Transaction, Long> {
    public List<Transaction> findAllByOrderByIdAsc();
}
