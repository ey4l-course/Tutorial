package com.reminder.Users.service;

import com.reminder.Users.model.IpAddress;
import com.reminder.Users.model.UserCrm;
import com.reminder.Users.model.UserLogin;
import com.reminder.Users.repository.UsersRepository;
import com.reminder.security.CustomUserDetails;
import com.reminder.Users.utilities.IpResolver;
import com.reminder.Users.utilities.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.regex.Pattern;

@Service
public class UsersService {
    @Autowired
    UsersRepository usersRepository;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    BCryptPasswordEncoder encoder;

    final private Pattern validEmail = Pattern.compile("^[a-zA-Z0-9-_~+.]{2,30}@[a-zA-Z0-9]{2,15}(\\.[a-zA-Z]{2,3}){1,2}$");
    final private Pattern validName = Pattern.compile("^(?=.{3,20}$)[a-zA-Z]{2,}(?: [a-zA-Z]{2,})*$");
    final private Pattern validMobile = Pattern.compile("^\\d{10,15}$");
    final private Pattern validUserName = Pattern.compile("^(?!.*( )\1)[a-zA-Z0-9 ._\\-$^~]{5,20}$");
    final private Pattern validPassword = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[-!@#$%^&*()_./]).{8,}$");

    public HashMap<String,String> newUser (UserLogin userCredentials, String ipAddress){
        //Every method either throws an exception or succeeds
        ipAddress = IpResolver.normalizeIp(ipAddress);
        usersRepository.isIpSus(ipAddress);
        validateCredentials(userCredentials);
        userCredentials.setHashedPassword(passwordHasher(userCredentials.getHashedPassword()));
        userCredentials.setRole("user");
        Long userId = usersRepository.save(userCredentials);
        usersRepository.logNewIp(new IpAddress(userId, ipAddress));
        HashMap<String,String> response = new HashMap<>();
        response.put("accessToken", jwtUtil.generateJwtToken(userCredentials.getUserName(), userCredentials.getRole()));
        response.put("refreshToken", jwtUtil.generateRefreshToken(userCredentials.getUserName(), userCredentials.getRole()));
        return response;
    }

    @Transactional
    public void newUserActivation (UserCrm userDetails){
        validateCrmDetails(userDetails);
        userDetails.setServiceLevel(determineServiceLevel(userDetails.getEmail(), userDetails.getMobile()));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails authUser = (CustomUserDetails) auth.getPrincipal();
        String userName = authUser.getUsername();
        Long id = usersRepository.activate(userDetails);
        usersRepository.updateLoginUserId(userName, id);
    }

    private void validateCredentials(UserLogin user){
        if (!validUserName.matcher(user.getUserName()).matches())
            throw new  IllegalArgumentException ("User name must contain letters, digits single space or ._-$^~");
        if (!validPassword.matcher(user.getHashedPassword()).matches())
            throw new IllegalArgumentException("Password must be 8-20 characters long and contain at least 1 upper case, 1 lower case, 1 digit and 1 symbol (-!@#$%^&*()_./)");
    }

    private void validateCrmDetails (UserCrm user){
        if (!validName.matcher(user.getGivenName()).matches())
            throw new IllegalArgumentException("Given name must be 3-20 character length, may include additional name separated by single space and cannot be blank");
        if (!validName.matcher(user.getSurname()).matches())
            throw new IllegalArgumentException("Surname must be 3-20 character length, may include additional name separated by single space and cannot be blank");
        if (!validEmail.matcher(user.getEmail()).matches() && user.getEmail() != null && !user.getEmail().isEmpty())
            throw new IllegalArgumentException("Invalid E-mail address");
        if (!validMobile.matcher(user.getMobile()).matches() && user.getMobile() != null && !user.getMobile().isEmpty())
            throw new IllegalArgumentException("Mobile must be 10-15 digit long, may include state prefix without + or separators");
    }

    private int determineServiceLevel (String email, String mobile){
        boolean condition1 = (email != null && !email.isEmpty());
        boolean condition2 = (mobile !=null && !mobile.isEmpty());
        if (condition1 && condition2)
            return 3;
        if (condition1 || condition2)
            return 2;
        else return 1;
    }

    private String passwordHasher (String rawPassword){
        return encoder.encode(rawPassword);
    }

//    private boolean isIpListed (String ipAddress, Long userId){
//        return usersRepository.isIpListed(ipAddress, userId);
//    }
}
