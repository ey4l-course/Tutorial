package com.reminder.Users.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table (name = "user_login")
public class UserLogin {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    @Column (name = "user_id", unique = true)
    private Long userId;
    @NotNull
    @Size (min = 5, max = 20)
    @Column (name = "user_name", nullable = false)
    private String userName;
    @NotNull
    @Column (name = "hashed_password", nullable = false)
    @JsonProperty("password")
    private String hashedPassword;
    @NotNull
    @Column (name = "role", nullable = false)
    private String role;
    /*
    Roles:
    user - may perform actions on their own account
    admin - may perform actions on all accounts
    app - applicative user (siem/elk agent)
     */
    @Column (name = "is_active")
    private boolean isActive;

    public UserLogin() {
    }

    public UserLogin(Long id, Long userId, String userName, String hashedPassword, String role, boolean isActive) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.hashedPassword = hashedPassword;
        this.role = role;
        this.isActive = isActive;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "UserLogin{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", hashedPassword='" + hashedPassword + '\'' +
                ", role='" + role +'\'' +
                ", isActive=" + isActive +
                '}';
    }
}
