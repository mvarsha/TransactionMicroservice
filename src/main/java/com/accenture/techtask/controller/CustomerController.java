package com.accenture.techtask.controller;

import com.accenture.techtask.entity.Customer;
import com.accenture.techtask.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CustomerController {
    @Autowired
    CustomerService customerService;

    @GetMapping("/customer/ids")
    public ResponseEntity<List<Long>> findAllCustomerIds() {
        List<Long> customers = customerService.findAllCustomerIds();
        return new ResponseEntity<List<Long>>(customers, HttpStatus.OK);
    }
}
