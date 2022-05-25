package com.accenture.techtask.service;

import com.accenture.techtask.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    public List<Long> findAllCustomerIds() {
        return customerRepository.findAllCustomerIds();
    }
}
