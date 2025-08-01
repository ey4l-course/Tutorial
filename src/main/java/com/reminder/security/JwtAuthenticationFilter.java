package com.reminder.security;

import com.reminder.JwtConfig;
import com.reminder.Users.model.AuthResponseDTO;
import com.reminder.Users.model.RequestContextDTO;
import com.reminder.Users.utilities.IpUtil;
import com.reminder.utilities.LogUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.UnknownHostException;
import java.time.Instant;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final AuthService authService;
    private final JwtConfig jwtConfig;
    private final LogUtil logger;

    public JwtAuthenticationFilter (AuthService authService,
                                    JwtConfig jwtConfig,
                                    LogUtil logger){
        this.authService = authService;
        this.jwtConfig = jwtConfig;
        this.logger = logger;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        RequestContextDTO contextDTO = new RequestContextDTO(request.getRequestURI(), request.getMethod(), request.getHeader("user-agent"));
        request.setAttribute("context", contextDTO);
        try {
            contextDTO.setIp(IpUtil.ExtractIp(request));
        } catch (UnknownHostException e) {
            contextDTO.setIp("[Unresolved]" + e.getMessage());
        } catch (Exception e) {
            String uuid = logger.error(e);
            contextDTO.setIp("[Unresolved]" + e.getMessage() + "LogID: " + uuid);
        }

        //If path is in exclusion list skip all validations
        if (authService.validateUri(request.getRequestURI())){
            filterChain.doFilter(request, response);
            return;
        }
        //Initialize responseDTO and set tokens
        AuthResponseDTO responseDTO = new AuthResponseDTO();
        responseDTO.setAccessToken(request.getHeader(jwtConfig.getHeader().getAccessHeader()));
        responseDTO.setRefreshToken(request.getHeader(jwtConfig.getHeader().getRefreshHeader()));

        //Set username and status code to responseDTO
        //May set error message
        authService.TokenUserNameHandler (responseDTO, jwtConfig.getHeader().getPrefix());
        contextDTO.setUserName(responseDTO.getUserName());

        if (responseDTO.getStatusCode() >= 200 && responseDTO.getStatusCode() <= 299){
            authService.setSecurityContext(responseDTO);
        }

        //If error detected set context outcome and EOL and log it, write servletResponse and commit it
        if (responseDTO.getStatusCode() < 200 || responseDTO.getStatusCode() > 299) {
            contextDTO.setOutcome("[REJECTED] at filter, status: " + responseDTO.getStatusCode() + ", " + responseDTO.getErrorMessage());
            contextDTO.setEndProcess(Instant.now());
            logger.logRequest(contextDTO);
            response.setStatus(responseDTO.getStatusCode());
            response.getWriter().write(responseDTO.getErrorMessage());
            return;
        }

        if (responseDTO.getAccessToken() != null && !responseDTO.getAccessToken().isEmpty()){
            response.setHeader("Authorization", responseDTO.getAccessToken());
            response.setHeader("Refresh", responseDTO.getRefreshToken());
        }
        try {
            filterChain.doFilter(request, response);
        }finally {
            contextDTO.setEndProcess(Instant.now());
            logger.logRequest(contextDTO);
        }
    }}
