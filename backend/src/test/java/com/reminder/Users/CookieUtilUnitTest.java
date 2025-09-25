package com.reminder.Users;

import com.reminder.Users.model.TokensDTO;
import com.reminder.utilities.CookieUtil;
import com.reminder.utilities.LogUtil;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.*;

public class CookieUtilUnitTest {
    @Test
    public void addCookies(){
        LogUtil logger = mock(LogUtil.class);
        MockHttpServletResponse response = new  MockHttpServletResponse();
        TokensDTO tokens = new TokensDTO("access12345", "refresh12345");
        CookieUtil util = new CookieUtil(logger);
        util.addAuthCookies(response, tokens);
        Cookie[] cookies = response.getCookies();
        assertEquals (2, cookies.length);

        Cookie access = findCookie(cookies, "access");
        assertNotNull(access);
        assertEquals("access12345", access.getValue());
        assertTrue(access.isHttpOnly());
        assertFalse(access.getSecure());
        assertEquals("/", access.getPath());
        assertEquals(900, access.getMaxAge());

        Cookie refresh = findCookie(cookies, "refresh");
        assertNotNull(refresh);
        assertEquals("refresh12345", refresh.getValue());
        assertTrue(refresh.isHttpOnly());
        assertFalse(refresh.getSecure());
        assertEquals("/", refresh.getPath());
        assertEquals(7200, refresh.getMaxAge());
    }

    @Test
    public void addIllegalCookie(){
        LogUtil logger = mock(LogUtil.class);
        MockHttpServletResponse response = new  MockHttpServletResponse();
        TokensDTO tokens = new TokensDTO(null, null);
        CookieUtil util = new CookieUtil(logger);
        util.addAuthCookies(response, tokens);

        assertDoesNotThrow(() -> util.addAuthCookies(response, tokens));
    }


    private Cookie findCookie(Cookie[] cookies, String name) {
        for (Cookie c : cookies) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }
}
