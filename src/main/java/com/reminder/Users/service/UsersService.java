package com.reminder.Users.service;

import com.reminder.Users.model.IpAddress;
import com.reminder.Users.model.UserCrm;
import com.reminder.Users.model.UserLogin;
import com.reminder.Users.repository.UsersRepository;
import com.reminder.Users.utilities.IpResolver;
import com.reminder.Users.utilities.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UsersService {
    @Autowired
    UsersRepository usersRepository;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    BCryptPasswordEncoder encoder;

    final private Pattern validEmailRegex = Pattern.compile("^[a-zA-Z0-9-_~+.]{2,30}@[a-zA-Z0-9]{2,15}(\\.[a-zA-Z]{2,3}){1,2}$");
    final private Pattern validUserName = Pattern.compile("^(?!.*( )\1)[a-zA-Z0-9 ._\\-$^~]{5,20}$");
    final private Pattern validPassword = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[-!@#$%^&*()_./]).{8,}$");

    public String newUser (UserLogin userCredentials, String ipAddress){
        //Every method either throws an exception or succeeds
        usersRepository.isIpSus(ipAddress);
        ValidateCredentials(userCredentials);
        userCredentials.setHashedPassword(passwordHasher(userCredentials.getHashedPassword()));
        Long userId = usersRepository.save(userCredentials);
        usersRepository.logNewIp(new IpAddress(userId, ipAddress));
        return jwtUtil.generateJwtToken(userCredentials.getUserName(), false);
    }

    public void newUserActivation (UserCrm userDetails){

    }

    private void ValidateCredentials (UserLogin user){
        if (!validUserName.matcher(user.getUserName()).matches())
            throw new  IllegalArgumentException ("User name must contain letters, digits single space or ._-$^~");
        if (!validPassword.matcher(user.getHashedPassword()).matches())
            throw new IllegalArgumentException("Password must be 8-20 characters long and contain at least 1 upper case, 1 lower case, 1 digit and 1 symbol (-!@#$%^&*()_./)");
    }

    private String passwordHasher (String rawPassword){
        return encoder.encode(rawPassword);
    }

//    private boolean isIpListed (String ipAddress, Long userId){
//        return usersRepository.isIpListed(ipAddress, userId);
//    }
}
