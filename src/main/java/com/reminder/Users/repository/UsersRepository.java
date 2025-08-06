package com.reminder.Users.repository;

import com.reminder.Users.model.*;
import com.reminder.Users.repository.mapper.UserCrmMapper;
import com.reminder.Users.repository.mapper.UserLoginMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
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
            KeyHolder keyHolder = new GeneratedKeyHolder();
            String sql = String.format("INSERT INTO %s (user_name, hashed_password, role) VALUES (?, ?, ?)", LOGIN);
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, user.getUserName());
                ps.setString(2, user.getHashedPassword());
                ps.setString(3, user.getRole());
                return ps;
            }, keyHolder);
            return keyHolder.getKeyAs(Long.class);
        }catch (DataIntegrityViolationException e){
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

    public void updateMyProfile(Long userId, UserUpdateDTO detailsDTO) {
        String sql = String.format("UPDATE %s SET email_address = ?, mobile = ?, service_level = ?, last_seen = ? WHERE id = ?", CRM);
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, detailsDTO.getEmail());
            ps.setString(2, detailsDTO.getMobile());
            ps.setInt(3, detailsDTO.getServiceLevel());
            ps.setTimestamp(4, Timestamp.from(Instant.now()));
            ps.setLong(5, userId);
            return ps;
        });
    }

    public UserCrm getUserProfileById(Long userId) {
        String sql = String.format("SELECT * FROM %s WHERE id = ?", CRM);
        String sqlUpdateLastSeen = String.format("UPDATE %s SET last_seen = ? WHERE id = ?", CRM);
        Timestamp now = Timestamp.from(Instant.now());
        UserCrm result = (UserCrm) jdbcTemplate.queryForObject(sql, new UserCrmMapper(), userId);
        jdbcTemplate.update(sqlUpdateLastSeen, now, userId);
        return result;
    }

    public UserCrm adminGetProfileById (Long id){
        String sql = String.format("SELECT * FROM %s WHERE id = ?", CRM);
        return  (UserCrm) jdbcTemplate.queryForObject(sql, new UserCrmMapper(), id);
    }

    public void resetPassword(PasswordResetDTO password) {
        String sql = String.format("UPDATE %s SET hashed_password = ? WHERE user_name = ?", LOGIN);
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, password.getHashedPassword());
            ps.setString(2, password.getUserName());
            return ps;
        });
    }

    public void deleteAccount(Long id) {
        String sql = String.format("DELETE FROM %s WHERE id = ?", CRM);
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setLong(1, id);
            return ps;
        });
    }

    public List<UserCrm> getAllProfiles() {
        String sql = "SELECT * FROM " + CRM;
        return jdbcTemplate.query(sql, new UserCrmMapper());
    }

    public List<UserCrm> searchProfile(SearchProfileDTO search) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM " + CRM + " WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (search.getGivenName() != null && !search.getGivenName().isEmpty()) {
            sqlBuilder.append(" AND given_name = ? ");
            params.add(search.getGivenName());
        }
        if (search.getSurname() != null && !search.getSurname().isEmpty()) {
            sqlBuilder.append(" AND surname = ? ");
            params.add(search.getSurname());
        }
        if (search.getServiceLevel() != 0) {
            sqlBuilder.append(" AND service_level = ?");
            params.add(search.getServiceLevel());
        }
        String sql = sqlBuilder.toString();
        return jdbcTemplate.query(sql,params.toArray(), new UserCrmMapper());
    }

    public void updateProfile(String sql, List<Object> params) {
        jdbcTemplate.update(sql, params.toArray());
    }

    public boolean checkStatus(Long id) {
        String sql = String.format("SELECT is_active from %s WHERE user_id = ?", LOGIN);
        return jdbcTemplate.queryForObject(sql, boolean.class, id);
    }
}
