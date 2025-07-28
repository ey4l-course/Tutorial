package com.reminder.Users.repository;

import com.reminder.Users.model.IpAddress;
import com.reminder.Users.model.UserCrm;
import com.reminder.Users.model.UserLogin;
import com.reminder.Users.repository.mapper.UserLoginMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class UsersRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;
    public UsersRepository (JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Value("${app.tables.UsersCredentials}")
    private String LOGIN;
    @Value("${app.tables.commonIpAddresses}")
    private String IP_TABLE;
    @Value("${app.tables.usersDetails}")
    private String CRM;

    public Long save (UserLogin user){
        try {
//            System.out.println("LOGIN value is: "+LOGIN);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            String sql = String.format("INSERT INTO %s (user_name, hashed_password, role) VALUES (?, ?, ?)", LOGIN);
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, user.getUserName());
                ps.setString(2, user.getHashedPassword());
                ps.setString(3, user.getRole());
//                System.out.println("ps is: "+ps);
                return ps;
            }, keyHolder);
            return keyHolder.getKeyAs(Long.class);
        }catch (DataIntegrityViolationException e){
//            e.printStackTrace();
            throw new IllegalArgumentException("User-name already taken");
        }
    }

    public UserLogin getUserByUserName (String userName){
        String sql = String.format("SELECT * FROM %s WHERE user_name = ?", LOGIN);
        List<UserLogin> list = jdbcTemplate.query(sql, new UserLoginMapper(), userName);
        if (list.size() != 1)
            throw new IllegalStateException("Expected exactly one user, but found " + list.size());
        return list.get(0);
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
        try {
            String sql = String.format("SELECT is_sus FROM %s WHERE ip_address = ?", IP_TABLE);
            if (Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, boolean.class, ipAddress)))
                throw new AccessDeniedException (String.format("Suspicious IP activity detected %s", ipAddress));
        }catch (EmptyResultDataAccessException e){
//         System.out.println("Caught");
        }
    }

    public Long activate(UserCrm userDetails) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = String.format("INSERT INTO %s (given_name, surname, email_address, mobile, service_level, last_seen) VALUES (?, ?, ?, ?, ?, ?)", CRM);
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, userDetails.getGivenName());
            ps.setString(2, userDetails.getSurname());
            ps.setString(3, userDetails.getEmail());
            ps.setString(4, userDetails.getMobile());
            ps.setInt(5, userDetails.getServiceLevel());
            ps.setTimestamp(6, Timestamp.from(Instant.now()));
            return ps;
        }, keyHolder);
        return keyHolder.getKeyAs(Long.class);
    }

    public void updateLoginUserId(String userName, Long id) {
        String sql = String.format("UPDATE %s SET user_id = ?, is_active = true WHERE user_name = ?", LOGIN);
        jdbcTemplate.update(sql, id, userName);
    }
}
