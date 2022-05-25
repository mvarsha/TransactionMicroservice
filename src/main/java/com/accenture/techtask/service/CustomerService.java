package com.accenture.techtask.service;

import com.accenture.techtask.entity.Customer;
import com.neovisionaries.i18n.CountryCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    public List<Long> findAllCustomerIds() {
        return customerRepository.findAllCustomerIds();
    }

    public List<Long> findAllCustomerIdsByCountry(CountryCode countryCode) {
        return customerRepository.findAllCustomerIdsByCountry(countryCode);
    }
}
