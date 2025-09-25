package com.reminder.security;

import com.reminder.Users.model.RequestContextDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        RequestContextDTO contextDTO = (RequestContextDTO) request.getAttribute("context");
        if (contextDTO != null)
            contextDTO.setOutcome("[REJECTED] status: 403, " + accessDeniedException.getMessage());
        response.sendError(403);
    }
}
