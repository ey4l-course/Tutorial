package com.reminder;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    private String secret;
    private Access access;
    private Refresh refresh;
    private Header header;
    private List<String> excludedPaths;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public Refresh getRefresh() {
        return refresh;
    }

    public void setRefresh(Refresh refresh) {
        this.refresh = refresh;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public List<String> getExcludedPaths() {
        return excludedPaths;
    }

    public void setExcludedPaths(List<String> excludedPaths) {
        this.excludedPaths = excludedPaths;
    }

    public static class Access {
        private int expiration;
        public int getExpiration () {return expiration;}
        public void setExpiration (int expiration) {this.expiration = expiration;}
    }

    public static class Refresh {
        private int expiration;
        public int getExpiration () {return expiration;}
        public void setExpiration (int expiration) {this.expiration = expiration;}
    }

    public static class Header {
        private String accessHeader;
        private String refreshHeader;
        private String prefix;
        public String getAccessHeader (){return accessHeader;}
        public void setAccessHeader (String accessHeader){this.accessHeader = accessHeader;}
        public String getRefreshHeader () {return refreshHeader;}
        public void setRefreshHeader (String refreshHeader){this.refreshHeader = refreshHeader;}
        public String getPrefix () {return prefix;}
        public void setPrefix (String prefix) {this.prefix = prefix;}
    }
}
