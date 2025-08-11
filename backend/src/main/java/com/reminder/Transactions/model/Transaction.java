package com.reminder.Transactions.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
public class Transaction {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column (name = "user_id")
    private Long userId;
    @NotNull
    @Column (name = "txn_time", nullable = false)
    private Instant txnTime;
    @NotNull
    @Size(max = 20)
    @Column (name = "description", nullable = false)
    private String description;
    @NotNull
    @Column (name = "amount", nullable = false)
    private BigDecimal amount;
    @NotNull
    @Column (name = "category_id", nullable = false)
    private Long category;
    @NotNull
    @Column (name = "category_source", nullable = false)
    private  CategorySource categorySource;
    @Column (name = "unique_weight")
    private int uniqueWeight;
    @NotNull
    @Size (max = 20)
    @Column (name = "payment_method", nullable = false)
    private String paymentMethod;
    @Column (name = "comment", nullable = true)
    @Size (max = 50)
    private String comment;
    @Column (name = "regular")
    private Boolean isRegular;

    public Transaction() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public @NotNull Long getUserId() {
        return userId;
    }

    public void setUserId(@NotNull Long userId) {
        this.userId = userId;
    }

    public @NotNull Instant getTxnTime() {
        return txnTime;
    }

    public void setTxnTime(@NotNull Instant txnTime) {
        this.txnTime = txnTime;
    }

    public @NotNull @Size(max = 20) String getDescription() {
        return description;
    }

    public void setDescription(@NotNull @Size(max = 20) String description) {
        this.description = description;
    }

    public @NotNull BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(@NotNull BigDecimal amount) {
        this.amount = amount;
    }

    public @NotNull Long getCategory() {
        return category;
    }

    public void setCategory(@NotNull Long category) {
        this.category = category;
    }

    public @NotNull CategorySource getCategorySource() {
        return categorySource;
    }

    public void setCategorySource(@NotNull CategorySource categorySource) {
        this.categorySource = categorySource;
    }

    public int getUniqueWeight() {
        return uniqueWeight;
    }

    public void setUniqueWeight(int uniqueWeight) {
        this.uniqueWeight = uniqueWeight;
    }

    public @NotNull @Size(max = 20) String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(@NotNull @Size(max = 20) String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public @Size(max = 50) String getComment() {
        return comment;
    }

    public void setComment(@Size(max = 50) String comment) {
        this.comment = comment;
    }

    public Boolean getRegular() {
        return isRegular;
    }

    public void setRegular(Boolean regular) {
        isRegular = regular;
    }
}

