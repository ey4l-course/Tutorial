package com.reminder.Transactions.model;

public class ClassUpdateDTO {
    Long txnId;
    Long userId;
    String description;
    Long category;

    public ClassUpdateDTO() {
    }

    public ClassUpdateDTO(Long txnId, Long userId, String description, Long category) {
        this.txnId = txnId;
        this.userId = userId;
        this.description = description;
        this.category = category;
    }

    public Long getTxnId() {
        return txnId;
    }

    public void setTxnId(Long txnId) {
        this.txnId = txnId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCategory() {
        return category;
    }

    public void setCategory(Long category) {
        this.category = category;
    }
}
