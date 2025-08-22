package com.reminder.Transactions.model;

public class GetTransactions {
    Long userId;
    String userRole;
    Long Category;
    Long wantedUser;

    public GetTransactions() {
    }

    public GetTransactions(Long userId, String userRole, Long category, Long wantedUser) {
        this.userId = userId;
        this.userRole = userRole;
        Category = category;
        this.wantedUser = wantedUser;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public Long getCategory() {
        return Category;
    }

    public void setCategory(Long category) {
        this.Category = category;
    }

    public Long getWantedUser() {
        return wantedUser;
    }

    public void setWantedUser(Long wantedUser) {
        this.wantedUser = wantedUser;
    }
}
