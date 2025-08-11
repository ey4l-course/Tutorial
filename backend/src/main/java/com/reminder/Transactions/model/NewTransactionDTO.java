package com.reminder.Transactions.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
public class NewTransactionDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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

}
