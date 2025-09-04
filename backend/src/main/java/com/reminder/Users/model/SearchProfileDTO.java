package com.reminder.Users.model;

public class SearchProfileDTO {
    private String givenName;
    private String surname;
    private int ServiceLevel;

    public SearchProfileDTO(String givenName, String surname, int serviceLevel) {
        this.givenName = givenName;
        this.surname = surname;
        ServiceLevel = serviceLevel;
    }

    public SearchProfileDTO() {
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

    public int getServiceLevel() {
        return ServiceLevel;
    }

    public void setServiceLevel(int serviceLevel) {
        ServiceLevel = serviceLevel;
    }
}
