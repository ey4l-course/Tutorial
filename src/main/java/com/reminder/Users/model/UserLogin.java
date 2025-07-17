package com.reminder.Users.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table (name = "user_login")
public class UserLogin {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column (name = "user_id", nullable = false, unique = true)
    private Long userId;
    @NotNull
    @Size (min = 5, max = 20)
    @Column (name = "user_name", nullable = false)
    private String userName;
    @NotNull
    @Column (name = "hashed_password", nullable = false)
    private String hashedPassword;
    @Column (name = "is_admin")
    private boolean isAdmin;
    @Column (name = "is_active")
    private boolean isActive;

    public UserLogin() {
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

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    @Override
    public String toString() {
        return "UserLogin{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", hashedPassword='" + hashedPassword + '\'' +
                ", isAdmin='" + isAdmin +'\'' +
                ", isActive=" + isActive +
                '}';
    }
}
