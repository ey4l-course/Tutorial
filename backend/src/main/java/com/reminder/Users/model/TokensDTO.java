package com.reminder.Users.model;

public class TokensDTO {
    private String accessToken;
    private String refreshToken;
    private boolean flag = true;

    public TokensDTO () {}

    public TokensDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
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

    public boolean isFlag () {return flag;}

    public void setFlag (boolean flag) {this.flag = flag;}

    @Override
    public String toString() {
        return "TokensDTO{" +
                "accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                '}';
    }
}
