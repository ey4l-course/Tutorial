package com.reminder.Users;

import com.reminder.Users.model.UserLogin;
import com.reminder.Users.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class GetUserByUserNameUnitTest {

    /*
    Verifies that a single user is returned when the database query returns exactly one record.
     */
/*
    @Test
    void testOfSuccessExactlyOneUserReturned(){
        JdbcTemplate jdbcTemplate = Mockito.mock(JdbcTemplate.class);
        UsersRepository repo = new UsersRepository(jdbcTemplate);

        UserLogin user = new UserLogin();
        user.setUserName("testUser");
        Mockito.when(jdbcTemplate.query(anyString(),any(RowMapper.class),eq("testUser")))
                .thenReturn(List.of(user));
        UserLogin result = repo.getUserByUserName("testUser");
    }

    @Test
    void testOfExceptionNoresult(){
        JdbcTemplate jdbcTemplate = Mockito.mock(JdbcTemplate.class);
        UsersRepository repo = new UsersRepository(jdbcTemplate);

        Mockito.when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq("NoUser")))
                .thenReturn(List.of());
        assertThrows(IllegalStateException.class, () -> {
            repo.getUserByUserName("NoUser");
        });
    }

 */
    @Test
    void testOfExceptionMultipleResults(){
        JdbcTemplate jdbcTemplate = Mockito.mock(JdbcTemplate.class);
        UsersRepository repo = new UsersRepository(jdbcTemplate);

        List<UserLogin> list = new ArrayList<>();
        list.add(0, new UserLogin());
        list.add(1, new UserLogin());

        Mockito.when(jdbcTemplate.query(anyString(),any(RowMapper.class), eq("testUser")))
                .thenReturn(list);

        assertThrows(IllegalStateException.class, () -> {
            repo.getUserByUserName("testUser");
        });
    }
}

