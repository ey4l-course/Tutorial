package com.reminder.Users.repository;

import com.reminder.Users.model.IpAddress;
import com.reminder.Users.model.UserLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.time.LocalDateTime;

@Repository
public class UsersRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    private final String LOGIN = "${app.tables.usersDetails}";
    private final String IP_TABLE = "${app.tables.commonIpAddresses}";

    public Long save (UserLogin user){
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            String sql = String.format("INSERT INTO %s (user_name, hashed_password) VALUES (?, ?)", LOGIN);
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, user.getUserName());
                ps.setString(2, user.getHashedPassword());
                return ps;
            }, keyHolder);
            return keyHolder.getKeyAs(Long.class);
        }catch (DataIntegrityViolationException e){
            throw new IllegalArgumentException("User-name already taken");
        }
    }

    public UserLogin getUserByUserName (String userName){
        String sql = String.format("SELECT * FROM %s WHERE user_name = ?", LOGIN);
        return jdbcTemplate.queryForObject(sql, UserLogin.class, userName);
    }

    public void logNewIp(IpAddress ip) {
        LocalDateTime timeNow = LocalDateTime.now();
        String sql;
        Object[] params;

        if (isIpListed(ip.getIpAddress(), ip.getUserId())) {
            sql = String.format(
                    "UPDATE %s SET last_seen = ?, usage_count = usage_count + 1 WHERE ip_address = ? AND user_id = ?", IP_TABLE);
            params = new Object[]{timeNow, ip.getIpAddress(), ip.getUserId()};
        } else {
            sql = String.format(
                    "INSERT INTO %s (last_seen, ip_address, user_id, usage_count) VALUES (?, ?, ?, ?)", IP_TABLE);
            params = new Object[]{timeNow, ip.getIpAddress(), ip.getUserId(), 1};
        }

        jdbcTemplate.update(sql, params);
    }


    private boolean isIpListed (String ipAddress, Long userId){
        String sql = String.format("SELECT EXISTS (SELECT 1 FROM %s WHERE ip_address = ? AND user_id = ?)", IP_TABLE);
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, boolean.class, ipAddress, userId));
    }

    public void isIpSus (String ipAddress){
        String sql = String.format("SELECT is_sus FROM %s WHERE ip_address = ?");
        if (Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, boolean.class, ipAddress)))
            throw new AccessDeniedException (String.format("Suspicious IP activity detected %s", ipAddress));
    }
}
