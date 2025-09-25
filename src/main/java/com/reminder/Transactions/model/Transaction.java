package com.reminder.Transactions.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {
    private int id;
    @JsonProperty(value = "txn_time")
    private LocalDate txnTime;
    private String description;
    private BigDecimal amount;
    private String category;
    private String comment;

    public Transaction() {
    }

    public Transaction(int id, LocalDate txnTime, String description, BigDecimal amount, String category, String comment) {
        this.id = id;
        this.txnTime = txnTime;
        this.description = description.toLowerCase();
        this.amount = amount;
        this.category = category.toLowerCase();
        this.comment = comment.toLowerCase();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getTxnTime() {
        return txnTime;
    }

    public void setTxnTime(LocalDate txnTime) {
        this.txnTime = txnTime;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category.toLowerCase();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment.toLowerCase();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description.toLowerCase();
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", txnTime=" + txnTime +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}

