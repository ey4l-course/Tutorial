package com.reminder.Users.model;

import java.time.Instant;

public class RequestContextDTO {
    private String ip;
    private String userAgent;
    private String userName;
    private String method;
    private String entryRoute;
    private String outcome;
    private Instant startProcess;
    private Instant endProcess;
//    private String referrer;  ***Possible future enhancement***

    public RequestContextDTO(String entryRoute, String method, String userAgent) {
        this.userName = "n/a";
        this.method = method;
        this.entryRoute = entryRoute;
        this.userAgent = userAgent;
        this.startProcess = Instant.now();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getEntryRoute() {
        return entryRoute;
    }

    public void setEntryRoute(String entryRoute) {
        this.entryRoute = entryRoute;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public Instant getStartProcess() {
        return startProcess;
    }

    public void setStartProcess(Instant startProcess) {
        this.startProcess = startProcess;
    }

    public Instant getEndProcess() {
        return endProcess;
    }

    public void setEndProcess(Instant endProcess) {
        this.endProcess = endProcess;
    }
}