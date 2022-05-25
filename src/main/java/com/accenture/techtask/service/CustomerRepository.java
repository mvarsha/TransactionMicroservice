package com.accenture.techtask.service;

import com.accenture.techtask.entity.Customer;
import com.neovisionaries.i18n.CountryCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("SELECT c.id FROM Customer c")
    List<Long> findAllCustomerIds();

    @Query("SELECT c.id FROM Customer c where c.countryCode = :code")
    List<Long> findAllCustomerIdsByCountry(@Param("code") CountryCode code);
}
