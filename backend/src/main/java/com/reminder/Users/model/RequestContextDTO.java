package com.reminder.Users.model;

import java.time.Instant;
import java.util.Arrays;

public class RequestContextDTO {
    private String category;
    private String ip;
    private String userAgent;
    private String userName;
    private String method;
    private String entryRoute;
    private String outcome;
    private int statusCode;
    private String statusMessage;
    private Instant startProcess;
    private Instant endProcess;
    private Exception debug;
//    private String referrer;  ***Possible future enhancement***

    public RequestContextDTO(String entryRoute, String method, String userAgent) {
        this.userName = "n/a";
        this.method = method;
        this.entryRoute = entryRoute;
        this.userAgent = userAgent;
        this.startProcess = Instant.now();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
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

    public Exception getDebug() {
        return debug;
    }

    public void setDebug(Exception debug) {
        this.debug = debug;
    }
}