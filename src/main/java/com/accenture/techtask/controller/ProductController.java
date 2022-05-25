package com.accenture.techtask.controller;

import com.accenture.techtask.entity.Product;
import com.accenture.techtask.entity.ProductStatus;
import com.accenture.techtask.entity.Transaction;
import com.accenture.techtask.service.ProductService;
import com.accenture.techtask.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class ProductController {
    @Autowired
    ProductService productService;

    @GetMapping("/products")
    public ResponseEntity<List<Product>> findProductsByStatusOrderByCostDesc(@RequestParam(value = "status") ProductStatus status) {
        List<Product> products = productService.findProductsByStatusOrderByCostDesc(status);
        return new ResponseEntity<List<Product>>(products, HttpStatus.OK);
    }
}
