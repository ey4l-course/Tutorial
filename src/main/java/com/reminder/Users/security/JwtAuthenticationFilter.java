package com.reminder.Users.security;

import com.reminder.Users.model.UserLogin;
import com.reminder.Users.utilities.JwtUtil;
import com.reminder.utilities.LogUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    LogUtil logUtil;
    @Autowired
    CustomUserDetailService userDetailService;

    @Value("${app.jwt.header.authHeader}")
    private String HEADER;

    @Value("${app.jwt.header.prefix}")
    private String PREFIX;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader(HEADER);
        String userName = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith(PREFIX)) {
            jwt = authHeader.substring(PREFIX.length());
            try {
                userName = jwtUtil.extractUserName(jwt);
            } catch (ExpiredJwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token expired");
            } catch (Exception e) {
                String uuid = logUtil.error(e);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid token ref.:" + uuid);
            }
        }
        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailService.loadUserByUsername(userName);
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                String uuid = logUtil.error(e);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid token ref.:" + uuid);
            }
        }
        filterChain.doFilter(request, response);
    }}
