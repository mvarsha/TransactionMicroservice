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

    public List<Transaction> findByCustomerId(Long customerId) {
        return transactionRepository.findByCustomerId(customerId);
    }

    public List<Transaction> findByCustomerIds(List<Long> customerIds) {
        return transactionRepository.findByCustomerIdIn(customerIds);
    }

    public List<Transaction> findByProductCode(String productCode) {
        return transactionRepository.findByProductCode(productCode);
    }
}