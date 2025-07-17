package com.reminder.Users.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;

@Entity
@Table (name = "user_crm")
public class UserCrm {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    @Size(min = 3, max = 20)
    @Pattern(regexp = "^[a-zA-Z]+( [a-zA-Z]+)*$")
    @Column(name = "given_name", nullable = false)
    private String givenName;
    @NotNull
    @Size(min = 3, max = 20)
    @Pattern(regexp = "^[a-zA-Z]+( [a-zA-Z]+)*$")
    @Column(name = "surname", nullable = false)
    private String surname;
    @Size(max = 40)
    @Email
    @Column(name = "email_address", unique = true)
    private String email;
    @Size(min = 10, max = 15)
    @Column(name = "mobile")
    private String mobile;
    @Column(name = "service_level")
    private int serviceLevel;
    @Column (name = "last_seen")
    private Instant lastSeen;

    public UserCrm() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getServiceLevel() {
        return serviceLevel;
    }

    public void setServiceLevel(int serviceLevel) {
        this.serviceLevel = serviceLevel;
    }

    public Instant getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Instant lastSeen) {
        this.lastSeen = lastSeen;
    }

    @Override
    public String toString() {
        return "UserCrm{" +
                "id=" + id +
                ", givenName='" + givenName + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", serviceLevel=" + serviceLevel +
                ", lastSeen=" + lastSeen +
                '}';
    }
}


