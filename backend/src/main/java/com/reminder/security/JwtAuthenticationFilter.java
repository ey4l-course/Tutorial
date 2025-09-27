package com.reminder.security;

import com.reminder.JwtConfig;
import com.reminder.Users.model.AuthResponseDTO;
import com.reminder.Users.model.RequestContextDTO;
import com.reminder.Users.model.TokensDTO;
import com.reminder.Users.utilities.IpUtil;
import com.reminder.utilities.CookieUtil;
import com.reminder.utilities.LogUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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
    private final CookieUtil cookieUtil;

    public JwtAuthenticationFilter (AuthService authService,
                                    JwtConfig jwtConfig,
                                    LogUtil logger,
                                    CookieUtil cookieUtil){
        this.authService = authService;
        this.jwtConfig = jwtConfig;
        this.logger = logger;
        this.cookieUtil = cookieUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        RequestContextDTO contextDTO = (RequestContextDTO) request.getAttribute("context");
        //If path is in exclusion list skip all validations
        boolean isExcluded = authService.validateUri(request.getRequestURI());
        try {
            if (!isExcluded) {
                //Initialize responseDTO and set tokens
                AuthResponseDTO responseDTO = new AuthResponseDTO();
                Cookie[] cookies = request.getCookies();
                for (Cookie c : cookies){
                    if (c.getName().equals("access") && c.getValue() != null && !c.getValue().isEmpty())
                        responseDTO.setAccessToken(c.getValue());
                    if (c.getName().equals("refresh") && c.getValue() != null && !c.getValue().isEmpty())
                        responseDTO.setRefreshToken(c.getValue());
                }
                if (responseDTO.getAccessToken().isEmpty() || responseDTO.getAccessToken() == null)
                    responseDTO.setAccessToken(request.getHeader(jwtConfig.getHeader().getAccessHeader()));
                if (responseDTO.getRefreshToken().isEmpty() || responseDTO.getRefreshToken() == null)
                    responseDTO.setRefreshToken(request.getHeader(jwtConfig.getHeader().getRefreshHeader()));

                //Set username and status code to responseDTO
                //May set error message
                authService.TokenUserNameHandler(responseDTO, jwtConfig.getHeader().getPrefix());
                contextDTO.setUserName(responseDTO.getUserName());
                if (responseDTO.getStatusCode() >= 200 && responseDTO.getStatusCode() <= 299) {
                    if ("tokens refreshed".equals(responseDTO.getErrorMessage())){
                        TokensDTO tokensDTO = new TokensDTO(responseDTO.getAccessToken(), responseDTO.getRefreshToken());
                        cookieUtil.addAuthCookies(response, tokensDTO);
                    }
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

                if (responseDTO.getAccessToken() != null && !responseDTO.getAccessToken().isEmpty()) {
                    response.setHeader("Authorization", responseDTO.getAccessToken());
                    response.setHeader("Refresh", responseDTO.getRefreshToken());
                }
            }
            filterChain.doFilter(request, response);
        }finally {
            contextDTO.setEndProcess(Instant.now());
            logger.logRequest(contextDTO);
        }
    }}
