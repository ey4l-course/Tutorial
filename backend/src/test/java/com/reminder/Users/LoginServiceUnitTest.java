package com.reminder.Users;


import com.reminder.Users.model.TokensDTO;
import com.reminder.Users.model.UserLogin;
import com.reminder.Users.repository.UsersRepository;
import com.reminder.Users.service.UsersService;
import com.reminder.Users.utilities.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.InvalidParameterException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceUnitTest {
    @Test
    public void SuccessfulLogin (){
        UsersRepository repo = Mockito.mock(UsersRepository.class);
        JwtUtil jwtUtil = Mockito.mock(JwtUtil.class);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        UsersService usersService = new UsersService(repo, jwtUtil, encoder);
        String testPassword = encoder.encode("G00dP@ssword");
        UserLogin savedUser = new UserLogin(1L, 1L, "ValidUser", testPassword, "user", true);
        UserLogin authUser = new UserLogin(null,null, "ValidUser", "G00dP@ssword", "user", true);

        Mockito.when(repo.getUserByUserName("ValidUser")).thenReturn(savedUser);
        Mockito.when(jwtUtil.generateJwtToken(anyString(), anyString())).thenReturn("Mock-Access-Token");
        Mockito.when(jwtUtil.generateRefreshToken(anyString(), anyString())).thenReturn("Mock-Refresh-Token");

        TokensDTO result = usersService.loginService(authUser);

        assertEquals("Mock-Access-Token", result.getAccessToken());
        assertEquals("Mock-Refresh-Token", result.getRefreshToken());
    }

    @Test
    public void notActiveLogin (){
        UsersRepository repo = Mockito.mock(UsersRepository.class);
        JwtUtil jwtUtil = Mockito.mock(JwtUtil.class);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        UsersService usersService = new UsersService(repo, jwtUtil, encoder);
        String testPassword = encoder.encode("G00dP@ssword");
        UserLogin savedUser = new UserLogin(1L, 1L, "ValidUser", testPassword, "user", false);
        UserLogin authUser = new UserLogin(null,null, "ValidUser", "G00dP@ssword", "user", true);

        Mockito.when(repo.getUserByUserName("ValidUser")).thenReturn(savedUser);

        assertThrows(InvalidParameterException.class, () -> {usersService.loginService(authUser);});
    }

    @Test
    public void wrongPasswordLogin (){
        UsersRepository repo = Mockito.mock(UsersRepository.class);
        JwtUtil jwtUtil = Mockito.mock(JwtUtil.class);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        UsersService usersService = new UsersService(repo, jwtUtil, encoder);
        String testPassword = encoder.encode("G00dP@ssword");
        UserLogin savedUser = new UserLogin(1L, 1L, "ValidUser", testPassword, "user", true);
        UserLogin authUser = new UserLogin(null,null, "ValidUser", "Bad1P@ssword", "user", true);

        Mockito.when(repo.getUserByUserName("ValidUser")).thenReturn(savedUser);

        assertThrows(AccessDeniedException.class, () -> {usersService.loginService(authUser);});
    }
}
