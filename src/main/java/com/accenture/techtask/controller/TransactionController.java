package com.accenture.techtask.controller;

import com.accenture.techtask.entity.Product;
import com.accenture.techtask.entity.Transaction;
import com.accenture.techtask.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    Map<String, Product> productMap;
    @Value("${server.port}")
    private String serverPort;
    public static final String baseURL = "http://localhost:";
    public static final int MAX_COST = 5000;
    public static final Logger logger = Logger.getLogger(TransactionController.class.getName());

    /**
     * Add a list of transactions
     * @param transactions List of transactions
     * @return HttpStatus.CREATED or HttpStatus.BAD_REQUEST
     */
    @PostMapping(value = "/transaction/add", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE },
            produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addTransactions(@Valid @RequestBody List<Transaction> transactions) {
        try {
            if(transactions.isEmpty()) {
                throw new InvalidTransactionException(new ErrorResponse("e-004", "No transactions to process"));
            }

            List<Transaction> validTransactions = filterValidTransactions(transactions);
            //Check cost
            validateCost(validTransactions);
            //Save valid transactions
            transactionService.saveTransactions(validTransactions);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (InvalidTransactionException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(e.getCode(), e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/transaction/cost/customer/{id}")
    public ResponseEntity<?> costByCustomerId(@PathVariable Long id) {
        try {
            List<Transaction> transactions = transactionService.findByCustomerId(id);
            List<Transaction> validTransactions = filterValidTransactions(transactions);
            long cost = calculateSum(validTransactions, false);
            return new ResponseEntity<Long>(cost, HttpStatus.OK);
        } catch (InvalidTransactionException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(e.getCode(), e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/transaction/cost/product/{productCode}")
    public ResponseEntity<?> costByProductCode(@PathVariable String productCode) {
        try {
            List<Transaction> transactions = transactionService.findByProductCode(productCode);
            List<Transaction> validTransactions = filterValidTransactions(transactions);
            long cost = calculateSum(validTransactions, false);
            return new ResponseEntity<Long>(cost, HttpStatus.OK);
        } catch (InvalidTransactionException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(e.getCode(), e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/transaction/count")
    public ResponseEntity<?> transactionsCountByCountry(@RequestParam(value = "country") String countryCode) {
        try {
            List<Long> customerIds = findCustomerIdsByCountry(countryCode);
            List<Transaction> transactions = transactionService.findByCustomerIds(customerIds);
            List<Transaction> validTransactions = filterValidTransactions(transactions);
            return new ResponseEntity<Integer>(validTransactions.size(), HttpStatus.OK);
        } catch (InvalidTransactionException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(e.getCode(), e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> allTransactions() {
        try {
            List<Transaction> transactions = transactionService.findAll();
            return new ResponseEntity<List<Transaction>>(transactions, HttpStatus.OK);
        } catch (InvalidTransactionException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(e.getCode(), e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    private List<Transaction> filterValidTransactions(List<Transaction> transactions) throws InvalidTransactionException {
        //Invoke microservice
        List<Product> products = findActiveProductsByStatusOrderByCostDesc();
        //Get product codes only
        productMap = products.stream().collect(Collectors.toMap(Product::getCode, product -> product));

        List<Long> customerIds = findAllCustomerIds();

        //Filter out Inactive product transactions
        List<Transaction> validTransactions =
                getActiveProductValidCustomerTransactionList(transactions, productMap.keySet(), customerIds);
        logger.warning(transactions.size() - validTransactions.size() +
                " transaction(s) dropped for INACTIVE products or invalid customers");

        if(validTransactions.isEmpty()) {
            throw new InvalidTransactionException(new ErrorResponse
                    ("e-002", "No valid transactions"));
        }

        return validTransactions;
    }

    private List<Product> findActiveProductsByStatusOrderByCostDesc() {
        ResponseEntity<Product[]> responseEntity = restTemplate.getForEntity
                (baseURL + serverPort + "/products?status=ACTIVE", Product[].class);
        if (responseEntity.getBody() != null) {
            return Arrays.stream(responseEntity.getBody()).toList();
        } else {
            logger.warning("No products");
            return new ArrayList<>();
        }
    }

    private List<Long> findAllCustomerIds() {
        ResponseEntity<Long[]> responseEntity = restTemplate.getForEntity(baseURL + serverPort + "/customer/ids", Long[].class);
        if (responseEntity.getBody() != null) {
            return Arrays.stream(responseEntity.getBody()).toList();
        } else {
            logger.warning("No customers");
            return new ArrayList<>();
        }
    }

    private List<Long> findCustomerIdsByCountry(String countryCode) {
        ResponseEntity<Long[]> responseEntity =
                restTemplate.getForEntity(baseURL + serverPort + "/customer/ids/" + countryCode, Long[].class);
        if (responseEntity.getBody() != null) {
            return Arrays.stream(responseEntity.getBody()).toList();
        } else {
            logger.warning("No customers");
            return new ArrayList<>();
        }
    }

    private void validateCost(List<Transaction> validTransactions) {
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
            long sum = calculateSum(validTransactions, true);
            if (sum > MAX_COST) {
                isTotalCostValid = false;
            }
        }
        if(!isTotalCostValid) {
            throw new InvalidTransactionException(new ErrorResponse("e-003", "Cost exceeds " + MAX_COST));
        }
    }

    private long calculateSum(List<Transaction> validTransactions, boolean shouldValidate) {
        long sum = 0;
        for (Transaction transaction: validTransactions) {
            Product product = productMap.get(transaction.getProductCode());
            if (product != null) {
                //Valid product
                sum += transaction.getQuantity() * product.getCost();
                if (shouldValidate && sum > MAX_COST) {
                    break;
                }
            }
        }
        return sum;
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
                return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
            }
        }
        return null;
    }
}