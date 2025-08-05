package com.reminder.Users.controller;

import com.reminder.Users.model.RequestContextDTO;
import com.reminder.Users.model.SearchProfileDTO;
import com.reminder.Users.model.UserCrm;
import com.reminder.Users.service.UsersService;
import com.reminder.utilities.LogUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private RequestContextDTO contextHandler (HttpServletRequest request){
        RequestContextDTO contextDTO = (RequestContextDTO) request.getAttribute("context");
        contextDTO.setUserName(SecurityContextHolder.getContext().getAuthentication().getName());
        return contextDTO;
    }
}
