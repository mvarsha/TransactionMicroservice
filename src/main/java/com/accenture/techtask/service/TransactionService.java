package com.accenture.techtask.service;

import com.accenture.techtask.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    public void saveTransactions(List<Transaction> transactions) {
        transactionRepository.saveAll(transactions);
    }
}