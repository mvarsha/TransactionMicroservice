package com.accenture.techtask.controller;

import com.accenture.techtask.entity.Customer;
import com.accenture.techtask.entity.Product;
import com.accenture.techtask.entity.Transaction;
import com.accenture.techtask.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * TransactionController
 */
@RestController
public class TransactionController {
    @Autowired
    TransactionService transactionService;
    RestTemplate restTemplate = new RestTemplate();
    public static final String baseURL = "http://localhost:8080/";
    public static final int MAX_COST = 5000;
    public static final Logger logger = Logger.getLogger(TransactionController.class.getName());

    /**
     * Add a list of transactions
     * @param transactions List of transactions in JSON
     * @return HttpStatus.CREATED or HttpStatus.NOT_FOUND
     */
    @PostMapping("/addTransactions")
    public ResponseEntity<?> addTransactions(@Valid @RequestBody List<Transaction> transactions) {
        try {
            //Invoke microservice
            List<Product> products = findActiveProductsByStatusOrderByCostDesc();
            //Get product codes only
            Map<String, Product> productMap =
                    products.stream().collect(Collectors.toMap(Product::getCode, product -> product));

            List<Long> customerIds = findAllCustomerIds();

            //Filter out Inactive product transactions
            List<Transaction> validTransactions =
                    getActiveProductValidCustomerTransactionList(transactions, productMap.keySet(), customerIds);
            logger.warning(transactions.size() - validTransactions.size() +
                    " transaction(s) dropped for INACTIVE products or invalid customers");

            //Check cost
            if(!validateCost(validTransactions, productMap)) {
                ErrorResponse errorResponse = new ErrorResponse("e-002", "Cost exceeds " + MAX_COST);
                return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.NOT_FOUND);
            }

            //Save valid transactions
            if (validTransactions.size() > 0) {
                transactionService.saveTransactions(validTransactions);
                return new ResponseEntity<>(HttpStatus.CREATED);
            } else {
                ErrorResponse errorResponse = new ErrorResponse("e-003", "No valid transactions to save");
                return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.NOT_FOUND);
            }
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private List<Product> findActiveProductsByStatusOrderByCostDesc() {
        ResponseEntity<Product[]> responseEntity = restTemplate.getForEntity(baseURL + "products?status=ACTIVE", Product[].class);
        if (responseEntity.getBody() != null) {
            return Arrays.stream(responseEntity.getBody()).toList();
        } else {
            logger.warning("No products");
            return new ArrayList<>();
        }
    }

    private List<Long> findAllCustomerIds() {
        ResponseEntity<Long[]> responseEntity = restTemplate.getForEntity(baseURL + "customer/ids", Long[].class);
        if (responseEntity.getBody() != null) {
            return Arrays.stream(responseEntity.getBody()).toList();
        } else {
            logger.warning("No customers");
            return new ArrayList<>();
        }
    }

    private boolean validateCost(List<Transaction> validTransactions, Map<String, Product> productMap) {
        boolean isTotalCostValid = true;

        if (validTransactions.size() > 50) {
            // 10 being the least cost, transactions > 50 will exceed 5000, reject
            isTotalCostValid = false;
        } else {
            /* Sort by product code so that the max cost threshold can be arrived at quickly
            validTransactions.sort(Comparator.comparingInt(transaction -> {
                int index = productCodes.indexOf(transaction.getProductCode());
                return index == -1? Integer.MAX_VALUE: index;
            }));*/
            int sum = 0;
            for (Transaction transaction: validTransactions) {
                Product product = productMap.get(transaction.getProductCode());
                if (product != null) {
                    //Valid product
                    sum += transaction.getQuantity() * product.getCost();
                    if (sum > MAX_COST) {
                        logger.warning("Cost more than " + MAX_COST + " breaking out, discarding transactions");
                        isTotalCostValid = false;
                        break;
                    }
                }
            }
        }

        return  isTotalCostValid;
    }

    public List<Transaction> getActiveProductValidCustomerTransactionList(List<Transaction> transactions,
                                                                          Set<String> productCodes,
                                                                          List<Long> customerIds) {
        return transactions.stream()
                .filter(transaction ->
                        (productCodes.contains(transaction.getProductCode())
                                && customerIds.contains(transaction.getCustomerId())))
                        .collect(Collectors.toList());
    }

    // Date can also be checked and that transaction can be skipped similar to cost and inactive products
    // If we wish to terminate the whole transaction, following method can be used
    @ExceptionHandler({ TransactionSystemException.class })
    public ResponseEntity<ErrorResponse> handleConstraintViolation(Exception ex, WebRequest request) {
        Throwable cause = ((TransactionSystemException) ex).getRootCause();
        if (cause instanceof ConstraintViolationException) {
            Set<ConstraintViolation<?>> constraintViolations = ((ConstraintViolationException) cause).getConstraintViolations();
            if(!constraintViolations.isEmpty()) {
                ErrorResponse errorResponse = new ErrorResponse("e-001", constraintViolations.iterator().next().getMessage());
                return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.NOT_FOUND);
            }
        }
        return null;
    }
}