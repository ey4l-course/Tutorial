package com.reminder.security;

import com.reminder.JwtConfig;
import com.reminder.Users.model.AuthResponseDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final AuthService authService;
    private final JwtConfig jwtConfig;

    public JwtAuthenticationFilter (AuthService authService,
                                    JwtConfig jwtConfig){
        this.authService = authService;
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (authService.validateUri(request.getRequestURI())){
            filterChain.doFilter(request, response);
            return;
        }

        AuthResponseDTO responseDTO = new AuthResponseDTO();
        responseDTO.setAccessToken(request.getHeader(jwtConfig.getHeader().getAccessHeader()));
        responseDTO.setRefreshToken(request.getHeader(jwtConfig.getHeader().getRefreshHeader()));

        authService.TokenUserNameHandler (responseDTO, jwtConfig.getHeader().getPrefix());

        if (responseDTO.getStatusCode() >= 200 && responseDTO.getStatusCode() <= 299){
            authService.setSecurityContext(responseDTO);
        }
        if (responseDTO.getStatusCode() < 200 || responseDTO.getStatusCode() > 299) {
            response.setStatus(responseDTO.getStatusCode());
            response.getWriter().write(responseDTO.getErrorMessage());
            return;
        }
        if (responseDTO.getAccessToken() != null && !responseDTO.getAccessToken().isEmpty()){
            response.setHeader("Authorization", responseDTO.getAccessToken());
            response.setHeader("Refresh", responseDTO.getRefreshToken());
        }
        filterChain.doFilter(request, response);
    }}
