package com.reminder.Users.model;

import java.util.HashMap;

public class AuthResponseDTO {
    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String userName;
    private String role;
    private boolean isActive;
    private int statusCode;
    private String errorMessage;

    public AuthResponseDTO() {
        this.accessToken = null;
        this.refreshToken = null;
        this.userId = null;
        this.userName = null;
        this.role = null;
        this.isActive = false;
        this.statusCode = 0;
        this.errorMessage =null;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "AuthResponseDTO{" +
                "accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", role='" + role + '\'' +
                ", isActive=" + isActive +
                ", statusCode=" + statusCode +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
