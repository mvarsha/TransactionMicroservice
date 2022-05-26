package com.accenture.techtask.service;

import com.accenture.techtask.entity.Product;
import com.accenture.techtask.entity.ProductStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<Product> findProductsByStatusOrderByCostDesc(ProductStatus status) {
        return productRepository.findByStatusOrderByCostDesc(status);
    }
}
