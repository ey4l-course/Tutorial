package com.reminder.Users.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

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
    @NotNull
    @Column (name = "last_seen", nullable = false)
    private Instant lastSeen;
    @NotNull
    @Column (name = "usage_count", nullable = false)
    private int usageCount;
    @NotNull
    @Column (name = "is_sus", nullable = false)
    private boolean isSus;

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

    public @NotNull Instant getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(@NotNull Instant lastSeen) {
        this.lastSeen = lastSeen;
    }

    @NotNull
    public int getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(@NotNull int usageCount) {
        this.usageCount = usageCount;
    }

    @NotNull
    public boolean isSus() {
        return isSus;
    }

    public void setSus(@NotNull boolean sus) {
        isSus = sus;
    }

    @Override
    public String toString() {
        return "IpAddress{" +
                "id=" + id +
                ", userId=" + userId +
                ", ipAddress='" + ipAddress + '\'' +
                ", lastSeen=" + lastSeen +
                ", usageCount=" + usageCount +
                ", isSus=" + isSus +
                '}';
    }
}