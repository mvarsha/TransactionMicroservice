package com.accenture.techtask.controller;

import com.accenture.techtask.entity.Customer;
import com.accenture.techtask.service.CustomerService;
import com.neovisionaries.i18n.CountryCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
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

    @GetMapping("/customer/ids/{countryCode}")
    public ResponseEntity<List<Long>> findAllCustomerIdsByCountry(@PathVariable("countryCode") String countryCode) {
        List<Long> customers = customerService.findAllCustomerIdsByCountry(CountryCode.getByCode(countryCode));
        return new ResponseEntity<List<Long>>(customers, HttpStatus.OK);
    }
}
