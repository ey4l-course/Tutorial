package com.reminder.Users.controller;

import com.reminder.Users.model.AuthResponseDTO;
import com.reminder.Users.model.TokensDTO;
import com.reminder.Users.model.UserCrm;
import com.reminder.Users.model.UserLogin;
import com.reminder.Users.service.UsersService;
import com.reminder.Users.utilities.JwtUtil;
import com.reminder.utilities.LogUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping ("/auth")
public class UsersController {
    @Autowired
    LogUtil logUtil;
    @Autowired
    UsersService usersService;
    @Autowired
    JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<?> newUserCredentials (@RequestBody UserLogin userLogin,
                                                 @RequestHeader (value = "X-Forwarded-For", required = false) String xForwardedFor,
                                                 HttpServletRequest request){
        try {
                    String ipAddress = (xForwardedFor != null && !xForwardedFor.isEmpty()
                    ? xForwardedFor.split(",")[0].trim()
                    : request.getRemoteAddr());
            HashMap<String,String> response = usersService.newUser(userLogin, ipAddress);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "user created",
                    "accessToken", response.get("accessToken"),
                    "refreshToken", response.get("refreshToken")));
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (AccessDeniedException e){
            String uuid = logUtil.securityLog(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied. If you believe you've been mistakenly blocked, please raise a ticket to support. log id: " + uuid);
        }catch (Exception e){
            String uuid = logUtil.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Mmmm this is awkward... Shouldn't happen. Please raise a ticket. log ID: " + uuid);
        }
    }

    @PreAuthorize("hasRole('user') or hasRole('admin')")
    @PostMapping("/activate")
    public ResponseEntity<?> newUserDetails (@RequestBody UserCrm userCrm){
        try {
            usersService.newUserActivation(userCrm);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("message", "account activated"));
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e){
            String uuid = logUtil.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Mmmm this is awkward... Shouldn't happen. Please raise a ticket. log ID: " + uuid);
        }
    }

    @PreAuthorize("hasRole('user') or hasRole('admin')")
    @PostMapping("/auth/login")
    public ResponseEntity<?> login (@RequestBody UserLogin user) {
        try {
            TokensDTO tokens = usersService.loginService (user);
            logUtil.infoLog(user.getUserName(), "Has successfully logged in");
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Login successful",
                    "accessToken", tokens.getAccessToken(),
                    "refreshToken", tokens.getRefreshToken()));
        }catch (Exception e){
            String uuid = logUtil.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Mmmm this is awkward... Shouldn't happen. Please raise a ticket. log ID: " + uuid);
        }
    }
}
