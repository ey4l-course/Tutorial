package com.reminder.security;

import com.reminder.Users.model.UserLogin;
import com.reminder.Users.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    UsersRepository usersRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserLogin userLogin = usersRepository.getUserByUserName(username);
        if (userLogin == null)
            throw new UsernameNotFoundException(String.format("User %s not found", username));
        return new CustomUserDetails(userLogin.getUserId(), userLogin.getUserName(), userLogin.getRole());
    }
}
