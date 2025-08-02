package com.reminder.Users.repository.mapper;

import com.reminder.Users.model.UserCrm;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

public class UserCrmMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserCrm user = new UserCrm();
        user.setGivenName(rs.getString("given_name"));
        user.setSurname(rs.getString("surname"));
        user.setEmail(rs.getString("email"));
        user.setMobile(rs.getString("mobile"));
        user.setServiceLevel(rs.getInt("service_level"));
        user.setLastSeen(rs.getTimestamp("last_seen").toInstant());
        return null;
    }
}