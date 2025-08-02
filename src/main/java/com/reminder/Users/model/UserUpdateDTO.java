package com.reminder.Users.model;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UserUpdateDTO {
    @Size(max = 40)
    @Email
    @Column(name = "email_address", unique = true)
    private String email;
    @Size(min = 10, max = 15)
    @Column(name = "mobile")
    private String mobile;
    @Column(name = "service_level")
    private int serviceLevel;

    public @Size(max = 40) @Email String getEmail() {
        return email;
    }

    public void setEmail(@Size(max = 40) @Email String email) {
        this.email = email;
    }

    public @Size(min = 10, max = 15) String getMobile() {
        return mobile;
    }

    public void setMobile(@Size(min = 10, max = 15) String mobile) {
        this.mobile = mobile;
    }

    public int getServiceLevel() {
        return serviceLevel;
    }

    public void setServiceLevel(int serviceLevel) {
        this.serviceLevel = serviceLevel;
    }
}
