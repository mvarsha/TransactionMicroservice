package com.accenture.techtask.service;

import com.accenture.techtask.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("SELECT id FROM app_customer")
    List<Long> findAllCustomerIds();
}
