package com.accenture.techtask.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "acc_transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name="customer_id")
    private Long customerId;
    @Column(name="quantity")
    private Integer quantity;
    @Column(name="product_code")
    private String productCode;
    @Column(name="transaction_time")
    private Date transactionTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Date getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(Date transactionTime) {
        this.transactionTime = transactionTime;
    }

    @Override
    public String toString() {
        return "com.accenture.techtask.entity.Transaction {" +
                "id=" + id +
                ", quantity='" + quantity + '\'' +
                ", productCode='" + productCode + '\'' +
                '}';
    }
}
