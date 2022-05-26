package com.accenture.techtask.entity;

import com.accenture.techtask.validator.OnAfterDateConstraint;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "acc_transaction")
public class Transaction implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @NotNull
    @Column(name="customer_id")
    private Long customerId;
    @NotNull
    @Column(name="quantity")
    private Integer quantity;
    @NotNull
    @Column(name="product_code")
    private String productCode;
    @NotNull
    @Column(name="transaction_time")
    @OnAfterDateConstraint
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private Date transactionTime;

    public Boolean validate() {
        return transactionTime.after(Date.from(Instant.now()));
    }

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
