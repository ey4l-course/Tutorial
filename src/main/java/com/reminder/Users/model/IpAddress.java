package com.reminder.Users.model;

import jakarta.persistence.*;

@Entity
@Table (name = "common_ip")
public class IpAddress {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    @Column (name = "user_id", nullable = false)
    private Long userId;
    @Column (name = "ip_address", nullable = false)
    private String ipAddress;

    public IpAddress() {
    }

    public IpAddress(Long userId, String ipAddress) {
        this.userId = userId;
        this.ipAddress = ipAddress;
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

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public String toString() {
        return "IpAddress{" +
                "id=" + id +
                ", userId=" + userId +
                ", IpAddress='" + ipAddress + '\'' +
                '}';
    }
}