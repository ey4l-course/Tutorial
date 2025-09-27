package com.reminder.security;

import com.reminder.Users.model.RequestContextDTO;
import com.reminder.Users.utilities.IpUtil;
import com.reminder.utilities.LogUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.UnknownHostException;
import java.time.Instant;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ContextInitFilter extends OncePerRequestFilter {
    private final LogUtil logger;

    public ContextInitFilter (LogUtil logger){this.logger = logger;}

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        RequestContextDTO contextDTO = new RequestContextDTO(request.getRequestURI(), request.getMethod(), request.getHeader("user-agent"));
        request.setAttribute("context", contextDTO);
        try {
            contextDTO.setIp(IpUtil.ExtractIp(request));
            filterChain.doFilter(request, response);
        } catch (UnknownHostException e) {
            contextDTO.setIp("[Unresolved]" + e.getMessage());
        } catch (Exception e) {
            contextDTO.setDebug(e);
        }finally {
            contextDTO.setEndProcess(Instant.now());
            logger.logRequest(contextDTO);
        }
    }
}
