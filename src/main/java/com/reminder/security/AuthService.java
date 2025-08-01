package com.reminder.security;

import com.reminder.JwtConfig;
import com.reminder.Users.model.AuthResponseDTO;
import com.reminder.Users.utilities.JwtUtil;
import com.reminder.utilities.LogUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

@Service
public class AuthService {
    private final JwtUtil jwtUtil;
    private final LogUtil logUtil;
    private final CustomUserDetailService userDetailService;
    private final AntPathMatcher matcher;
    private final JwtConfig jwtConfig;
    public AuthService (JwtUtil jwtUtil,
                        LogUtil logUtil,
                        CustomUserDetailService userDetailService,
                        AntPathMatcher matcher,
                        JwtConfig jwtConfig){
        this.jwtUtil = jwtUtil;
        this.logUtil = logUtil;
        this.userDetailService = userDetailService;
        this.matcher = matcher;
        this.jwtConfig = jwtConfig;
    }

    public void TokenUserNameHandler(AuthResponseDTO dto, String PREFIX){
        try {
            validateHeaders(dto.getAccessToken(), dto.getRefreshToken(), PREFIX);
            dto.setUserName(jwtUtil.extractUserName(dto.getAccessToken().substring(PREFIX.length())));
            dto.setRole(jwtUtil.extractRole(dto.getAccessToken().substring(PREFIX.length())));
            dto.setStatusCode(200);
        }catch (AuthenticationException e){
            dto.setStatusCode(403);
            dto.setErrorMessage(e.getMessage());
        }catch (ExpiredJwtException e) {
            try {
                dto.setUserName(jwtUtil.extractUserName(dto.getRefreshToken().substring(PREFIX.length())));
                dto.setRole(jwtUtil.extractRole(dto.getRefreshToken().substring(PREFIX.length())));
                logUtil.infoLog(dto.getUserName(), e.getMessage() + "New token successfully generated");
                dto.setAccessToken(jwtUtil.generateJwtToken(dto.getUserName(), dto.getRole()));
                dto.setRefreshToken(jwtUtil.generateRefreshToken(dto.getUserName(), dto.getRole()));
                dto.setStatusCode(200);
            }catch (ExpiredJwtException ex){
                logUtil.infoLog(dto.getUserName(), e.getMessage() + "Refresh token expired");
                dto.setStatusCode(401);
                dto.setErrorMessage(e.getMessage());
            }
        } catch (Exception e) {
            String uuid = logUtil.error(e);
            dto.setStatusCode(400);
            dto.setErrorMessage("Invalid token ref.:" + uuid);
        }
    }

    public void setSecurityContext (AuthResponseDTO dto){
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(currentAuth);
        CustomUserDetails userDetails = userDetailService.loadUserByUsername(dto.getUserName());
        Authentication newAuth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        if (currentAuth != null) {
            if (!currentAuth.getName().equals(dto.getUserName())) {
                String uuid = logUtil.securityLog(String.format("[Suspicious activity],Possible context leak or hijack,Expected '%s' found '%s'", dto.getUserName(), currentAuth.getName()));
                logUtil.criticalLog(String.format("Context populated with '%s' while authenticating '%s'. SIEM log: %s", currentAuth.getName(), dto.getUserName(), uuid));
                SecurityContextHolder.clearContext();
            }
        }
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    protected boolean validateUri (String currentPath){
        currentPath = currentPath.split("\\?")[0]; //Remove search parameters
        if (!currentPath.equals("/") && currentPath.endsWith("/"))
            currentPath = currentPath.substring(0, currentPath.length() - 1); // removes trailing "/" if not root
        for (String pattern : jwtConfig.getExcludedPaths())
            if (matcher.match(pattern, currentPath))
                return true;
        return false;
    }

    private void validateHeaders (String accessHeader, String refreshHeader, String PREFIX) throws Exception{
        if (accessHeader == null || !accessHeader.startsWith(PREFIX)){
            String uuid = logUtil.securityLog("Access token missing or corrupted. token: " + accessHeader);
            throw new BadCredentialsException( "Authentication failed, ref. ID: " + uuid);
        }
        if (refreshHeader == null || !refreshHeader.startsWith(PREFIX)){
            String uuid = logUtil.securityLog("Refresh token missing or corrupted. token: " + refreshHeader);
            throw new BadCredentialsException( "Authentication failed, ref. ID: " + uuid);
        }
    }
}
