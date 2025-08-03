package com.reminder.Users.controller;

import com.reminder.Users.model.*;
import com.reminder.Users.service.UsersService;
import com.reminder.Users.utilities.JwtUtil;
import com.reminder.security.CustomUserDetails;
import com.reminder.utilities.LogUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
                                                 HttpServletRequest request){
        RequestContextDTO contextDTO = (RequestContextDTO) request.getAttribute("context");
        try {
            contextDTO.setUserName(userLogin.getUserName());
            TokensDTO response = usersService.newUser(userLogin);
            contextDTO.setOutcome("[SUCCESS] status: 201, User created");
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "user created",
                    "accessToken", response.getAccessToken(),
                    "refreshToken", response.getRefreshToken()));
        }catch (IllegalArgumentException e) {
            contextDTO.setOutcome("[REJECTED] status 400, " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (AccessDeniedException e){
            contextDTO.setOutcome("[REJECTED] status 403, " + e.getMessage());
            String uuid = logUtil.securityLog(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied. If you believe you've been mistakenly blocked, please raise a ticket to support. log id: " + uuid);
        }catch (Exception e){
            contextDTO.setOutcome("[REJECTED] status 500, " + e.getMessage());
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

    @PreAuthorize("hasRole('user') or hasRole('admin') or hasRole(app)")
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

    @PreAuthorize("hasRole('user') or hasRole('admin')")
    @PatchMapping("/self_service")
    public ResponseEntity<?> editMyself (@RequestBody UserUpdateDTO detailsDTO,
                                         HttpServletRequest request){
        RequestContextDTO contextDTO = (RequestContextDTO) request.getAttribute("context");
        try {
            detailsDTO.setServiceLevel(0);
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = userDetails.getUserId();
            contextDTO.setUserName(userName);
            usersService.updateMyProfile(userId, detailsDTO);
            logUtil.infoLog(userName, "has successfully updated profile");
            contextDTO.setOutcome("[SUCCESS] status: 200, User profile updated");
            return ResponseEntity.status(HttpStatus.OK).body("User updated");
        }catch (IllegalArgumentException e){
            contextDTO.setOutcome("[REJECTED] status 400, " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e){
            contextDTO.setOutcome("[REJECTED] status 500, " + e.getMessage());
            String uuid = logUtil.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Mmmm this is awkward... Shouldn't happen. Please raise a ticket. log ID: " + uuid);
        }
    }

    @PreAuthorize("hasRole('user') or hasRole('admin')")
    @GetMapping("/self_service")
    public ResponseEntity<?> LoadMyProfile(HttpServletRequest request){
        RequestContextDTO contextDTO = (RequestContextDTO) request.getAttribute("context");
        try {
            CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
            Long userId = userDetails.getUserId();
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            contextDTO.setUserName(userName);
            UserCrm myProfile = usersService.viewMyProfile(userId);
            logUtil.infoLog(userName, "profile was successfully loaded by user");
            contextDTO.setOutcome("[SUCCESS] status: 200, User profile viewed by user");
            return ResponseEntity.status(HttpStatus.OK).body(myProfile);
        }catch (Exception e){
            contextDTO.setOutcome("[REJECTED] status 500, " + e.getMessage());
            String uuid = logUtil.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Mmmm this is awkward... Shouldn't happen. Please raise a ticket. log ID: " + uuid);
        }
    }

    @PreAuthorize("hasRole('user') or hasRole('admin')")
    @PatchMapping("/self_service/reset_password")
    public ResponseEntity<?> resetPassword (@RequestBody PasswordResetDTO password,
                                            HttpServletRequest request){
        RequestContextDTO contextDTO = (RequestContextDTO) request.getAttribute("context");
        try {
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            contextDTO.setUserName(userName);
            password.setUserName(userName);
            usersService.resetPassword(password);
            String uuid = logUtil.securityLog(userName + "Password successfully changed.");
            contextDTO.setOutcome("[SUCCESS] status: 200, User password successfully changed, uuid: " + uuid);
            return ResponseEntity.status(HttpStatus.OK).body("Password successfully changed. log ID: "+ uuid);
        }catch (IllegalArgumentException e){
            contextDTO.setOutcome("[REJECTED] status 400, " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e){
            contextDTO.setOutcome("[REJECTED] status 500, " + e.getMessage());
            String uuid = logUtil.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Mmmm this is awkward... Shouldn't happen. Please raise a ticket. log ID: " + uuid);
        }
    }
}
