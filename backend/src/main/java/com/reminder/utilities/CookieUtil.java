package com.reminder.utilities;

import com.reminder.Users.model.TokensDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
    private final LogUtil logger;
    public CookieUtil (LogUtil logger){this.logger = logger;}

    public void addAuthCookies (HttpServletResponse response, TokensDTO tokens) {
        try {
            Cookie accessCookie = new Cookie("access", tokens.getAccessToken());
            accessCookie.setHttpOnly(true);
            accessCookie.setSecure(false); //TODO: for prod set true
            accessCookie.setPath("/");
            accessCookie.setMaxAge(900);
            response.addCookie(accessCookie);

            Cookie refreshCookie = new Cookie("refresh", tokens.getRefreshToken());
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(false); //TODO: for prod set true
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(7200);
            response.addCookie(refreshCookie);
        }catch (IllegalArgumentException e){
            logger.error(e);
        }
    }
}
