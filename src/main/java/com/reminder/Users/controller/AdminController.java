package com.reminder.Users.controller;

import com.reminder.Users.model.*;
import com.reminder.Users.service.UsersService;
import com.reminder.security.CustomUserDetails;
import com.reminder.utilities.LogUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final UsersService usersService;
    private final LogUtil logUtil;
    public AdminController (UsersService usersService,
                            LogUtil logUtil){
        this.usersService = usersService;
        this.logUtil = logUtil;
    }

    @GetMapping
    public ResponseEntity<?> getAllProfiles (HttpServletRequest request){
        RequestContextDTO contextDTO = contextHandler(request);
        try {
            List<UserCrm> result = usersService.getAllProfiles();
            contextDTO.setOutcome("[SUCCESS] status: 200, " + result.size() + " entries retrieved");
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }catch (Exception e){
            contextDTO.setOutcome("[REJECTED] status: 500, " + e.getMessage());
            String uuid = logUtil.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Mmmm this is awkward... Shouldn't happen. Please raise a ticket. log ID: " + uuid);
        }
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getProfileById (@PathVariable Long id,
                                             HttpServletRequest request){
        RequestContextDTO contextDTO = contextHandler(request);
        try {
            UserCrm result = usersService.getProfileById(id);
            contextDTO.setOutcome("[SUCCESS] status: 200, Profile retrieved for: " + result.getGivenName() + " " + result.getSurname());
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }catch (EmptyResultDataAccessException e){
            contextDTO.setOutcome("[Failed] status: 404. User with ID " + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID " + " not found");
        }catch (Exception e){
            contextDTO.setOutcome("[REJECTED] status: 500, " + e.getMessage());
            String uuid = logUtil.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Mmmm this is awkward... Shouldn't happen. Please raise a ticket. log ID: " + uuid);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> GetProfilesByQuery (@RequestParam(value = "given-name", required = false) String givenName,
                                                 @RequestParam(value = "surname", required = false) String surname,
                                                 @RequestParam(value = "service-level", required = false) Integer serviceLevel,
                                                 HttpServletRequest request){
        RequestContextDTO contextDTO = contextHandler(request);
        if (givenName == null && surname == null && serviceLevel == null){
            contextDTO.setOutcome("[REJECTED] status: 400, Invalid search parameters");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid search parameters");
        }
        try {
            int level = serviceLevel == null ? 0 : serviceLevel;
            SearchProfileDTO search = new SearchProfileDTO(givenName, surname, level);
            List<UserCrm> result = usersService.searchProfile(search);
            contextDTO.setOutcome("[SUCCESS] status: 200, " + result.size() + " entries retrieved");
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }catch (Exception e){
            contextDTO.setOutcome("[REJECTED] status: 500, " + e.getMessage());
            String uuid = logUtil.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Mmmm this is awkward... Shouldn't happen. Please raise a ticket. log ID: " + uuid);
        }
    }

    @PatchMapping
    public ResponseEntity<?> editUserProfile (@RequestBody (required = false) AdminEditProfileDTO userProfile,
                                              HttpServletRequest request){
        RequestContextDTO contextDTO = contextHandler(request);
        try {
            Long userId = userProfile.getId();
            usersService.updateUserProfile(userProfile, userId);
            contextDTO.setOutcome("[SUCCESS] status: 202, User " + userId + "successfully updated");
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("User " + userId + "Successfully updated.");
        }catch (IllegalArgumentException e){
            contextDTO.setOutcome("[Failed] status: 422, " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        }catch (Exception e){
            contextDTO.setOutcome("[REJECTED] status: 500, " + e.getMessage());
            System.out.println(e);
            String uuid = logUtil.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Mmmm this is awkward... Shouldn't happen. Please raise a ticket. log ID: " + uuid);
        }
    }

    @PatchMapping("/activation/{id}/{isActive}")
    public ResponseEntity<?> activateDeactivate (@PathVariable Long id,
                                                 @PathVariable boolean isActive,
                                                 HttpServletRequest request){
        RequestContextDTO contextDTO = contextHandler(request);
        try {
            String result = usersService.setActiveStatus(id, isActive);
            contextDTO.setOutcome("[SUCCESS] status 202, user " + id + " " + result);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(result);
        }catch (IllegalArgumentException e){
            contextDTO.setOutcome("[FAILED] status 409, " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }catch (Exception e){
            contextDTO.setOutcome("[REJECTED] status: 500, " + e.getMessage());
            System.out.println(e);
            String uuid = logUtil.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Mmmm this is awkward... Shouldn't happen. Please raise a ticket. log ID: " + uuid);
        }
    }

    @PutMapping
    public ResponseEntity<?> createSpecialUser (@RequestBody UserLogin user,
                                                HttpServletRequest request){
        //Create admin or app user
    RequestContextDTO contextDTO = contextHandler(request);
        try {
            if (user.getRole() == "user") {
                String uuid = logUtil.securityLog(contextDTO.getUserName() + " Attempted to create regular user " + user.toString());
                contextDTO.setOutcome("[REJECTED] status: 403, Violation detected. security log: " + uuid);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Incident reported ref: " + uuid);
            }
            user.setId(usersService.newSpecialUser(user));
            contextDTO.setOutcome("[SUCCESS] status 201, User created: " + user.toString());
            return ResponseEntity.status(HttpStatus.CREATED).body("User created and assigned with ID: " + user.getId());
        }catch (IllegalArgumentException e){
            contextDTO.setOutcome("[REJECTED] status 400, " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e){
            contextDTO.setOutcome("[REJECTED] status: 500, " + e.getMessage());
            System.out.println(e);
            String uuid = logUtil.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Mmmm this is awkward... Shouldn't happen. Please raise a ticket. log ID: " + uuid);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAccountByAdmin (DeleteAccountDTO dto,
                                                   HttpServletRequest request){
        RequestContextDTO contextDTO = contextHandler(request);
        try {
            dto.setUserName(SecurityContextHolder.getContext().getAuthentication().getName());
            usersService.deleteAccount(dto);
            String uuid = logUtil.securityLog(dto.getUserName() + "successfully deleted the account.");
            contextDTO.setOutcome("[SUCCESS] status: 200, User account successfully deleted, uuid: " + uuid);
            return ResponseEntity.status(HttpStatus.OK).body("User successfully deleted, uuid: " + uuid);
        }catch (AccessDeniedException e){
            String uuid = logUtil.securityLog(e.getMessage());
            contextDTO.setOutcome("[REJECTED] status 403, " + e.getMessage() + ", uuid: " + uuid);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied. If you believe you've been mistakenly blocked, please raise a ticket to support. log id: " + uuid);
        }catch (Exception e){
            contextDTO.setOutcome("[REJECTED] status: 500, " + e.getMessage());
            System.out.println(e);
            String uuid = logUtil.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Mmmm this is awkward... Shouldn't happen. Please raise a ticket. log ID: " + uuid);
        }
    }


    private RequestContextDTO contextHandler (HttpServletRequest request){
        RequestContextDTO contextDTO = (RequestContextDTO) request.getAttribute("context");
        contextDTO.setUserName(SecurityContextHolder.getContext().getAuthentication().getName());
        return contextDTO;
    }
}
