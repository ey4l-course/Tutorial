package com.reminder.Users.repository.mapper;

import com.reminder.Users.model.UserLogin;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserLoginMapper implements RowMapper {
    @Override
    public UserLogin mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserLogin userLogin = new UserLogin();
        userLogin.setUserId(rs.getLong("id"));
        userLogin.setUserId(rs.getLong("user_id"));
        userLogin.setUserName(rs.getString("user_name"));
        userLogin.setHashedPassword(rs.getString("hashed_password"));
        userLogin.setRole(rs.getString("role"));
        userLogin.setActive(rs.getBoolean("is_active"));
        return userLogin;
    }
}