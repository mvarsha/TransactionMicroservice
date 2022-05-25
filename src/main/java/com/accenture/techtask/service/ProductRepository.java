package com.accenture.techtask.service;

import com.accenture.techtask.entity.Product;
import com.accenture.techtask.entity.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStatusOrderByCostDesc(ProductStatus status);
}
