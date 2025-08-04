package com.reminder.Users.controller;

import com.reminder.Users.model.RequestContextDTO;
import com.reminder.Users.model.UserCrm;
import com.reminder.Users.service.UsersService;
import com.reminder.utilities.LogUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    private RequestContextDTO contextHandler (HttpServletRequest request){
        RequestContextDTO contextDTO = (RequestContextDTO) request.getAttribute("context");
        contextDTO.setUserName(SecurityContextHolder.getContext().getAuthentication().getName());
        return contextDTO;
    }
}
