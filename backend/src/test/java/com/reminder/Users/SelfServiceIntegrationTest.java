package com.reminder.Users;

import com.reminder.Users.model.UserCrm;
import com.reminder.Users.model.UserLogin;
import com.reminder.Users.repository.UsersRepository;
import com.reminder.Users.repository.mapper.UserCrmMapper;
import com.reminder.Users.repository.mapper.UserLoginMapper;
import com.reminder.Users.utilities.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class SelfServiceIntegrationTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    BCryptPasswordEncoder encoder;
    @Autowired
    UsersRepository repo;
    @Autowired
    JdbcTemplate jdbc;
    @Autowired
    JwtUtil jwtUtil;

    @BeforeEach
    void setup() {
        jdbc.execute("DELETE FROM common_ip");
        jdbc.execute("DELETE FROM user_login");
        jdbc.execute("DELETE FROM user_crm");
        String hashedPassword = encoder.encode("V@lidP@$$w0rd");
        UserLogin userLogin = new UserLogin(null,null,"ValidUser1", hashedPassword, "user", false);
        UserCrm userCrm = new UserCrm(null, "John", "Doe", null, null, 0, null);
        repo.save(userLogin);
        Long userId = repo.activate(userCrm);
        repo.updateLoginUserId(userLogin.getUserName(), userId);
    }

    @Test
    void testSetup () throws InterruptedException {
        String sql = "SELECT * FROM user_crm";
        String sql2 = "SELECT * FROM user_login";
        System.out.println(jdbc.query(sql, new UserCrmMapper()));
        System.out.println(jdbc.query(sql2, new UserLoginMapper()));
    }

    //Valid request
    @Test
    void happyPath () throws Exception {
        String access = jwtUtil.generateJwtToken("ValidUser1", "user");
        String refresh = jwtUtil.generateRefreshToken("ValidUser1", "user");
        String update1 = "{\"email\": \"validmail@domain.com\"}";
        String update2 = "{\"mobile\": \"0523214565\"}";

        mockMvc.perform(get("/auth/self_service")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + access)
                .header("Refresh", "Bearer " + refresh))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.givenName").value("John"));

        mockMvc.perform(patch("/auth/self_service")
                .contentType(MediaType.APPLICATION_JSON)
                        .content(update1)
                .header("Authorization", "Bearer " + access)
                .header("Refresh", "Bearer " + refresh))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("User updated"));

        mockMvc.perform(get("/auth/self_service")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + access)
                        .header("Refresh", "Bearer " + refresh))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceLevel").value(2));

        mockMvc.perform(patch("/auth/self_service")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(update2)
                        .header("Authorization", "Bearer " + access)
                        .header("Refresh", "Bearer " + refresh))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("User updated"));

        mockMvc.perform(get("/auth/self_service")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + access)
                        .header("Refresh", "Bearer " + refresh))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceLevel").value(3));
    }

    @Test
    void updateInvalidFormat () throws Exception{
        String access = jwtUtil.generateJwtToken("ValidUser1", "user");
        String refresh = jwtUtil.generateRefreshToken("ValidUser1", "user");
        String update1 = "{\"email\": \"invalidmail.domain.com\"}";
        String update2 = "{\"mobile\": \"052-3214565\"}";

        mockMvc.perform(patch("/auth/self_service")
                .contentType(MediaType.APPLICATION_JSON)
                .content(update1)
                .header("Authorization", "Bearer " + access)
                .header("Refresh", "Bearer " + refresh))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Invalid E-mail address"));

        mockMvc.perform(patch("/auth/self_service")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(update2)
                        .header("Authorization", "Bearer " + access)
                        .header("Refresh", "Bearer " + refresh))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Mobile must be 10-15 digit long, may include state prefix without + or separators"));
    }

    @Test
    void resetPasswordHappyPath() throws Exception {
        String access = jwtUtil.generateJwtToken("ValidUser1", "user");
        String refresh = jwtUtil.generateRefreshToken("ValidUser1", "user");
        String newPassword = "{\"password\": \"V@l1dP@ssw0rd\"}";
        String oldHashed = jdbc.queryForObject("SELECT hashed_password FROM user_login WHERE user_name = 'ValidUser1'", String.class);
        mockMvc.perform(patch("/auth/self_service/reset_password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newPassword)
                .header("Authorization", "Bearer " + access)
                .header("Refresh", "Bearer " + refresh))
                .andExpect(status().isOk());

        String newHashed = jdbc.queryForObject("SELECT hashed_password FROM user_login WHERE user_name = 'ValidUser1'", String.class);
        assertNotEquals(oldHashed, newHashed);
    }

    @Test
    void resetPasswordInvalidPassword() throws Exception {
        String access = jwtUtil.generateJwtToken("ValidUser1", "user");
        String refresh = jwtUtil.generateRefreshToken("ValidUser1", "user");
        String[] newPassword = {"", null, "invalid", "Ty@2", "VeryComplicated12"};
        for (String password : newPassword) {
            String passwordJson = String.format("{\"password\":\"%s\"}",password);
            mockMvc.perform(patch("/auth/self_service/reset_password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(passwordJson)
                            .header("Authorization", "Bearer " + access)
                            .header("Refresh", "Bearer " + refresh))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$").value("Password must be 8-20 characters long and contain at least 1 upper case, 1 lower case, 1 digit and 1 symbol (-!@#$%^&*()_./)"));
        }
    }

    @Test
    void deleteHappyPath() throws Exception{
        String access = jwtUtil.generateJwtToken("ValidUser1", "user");
        String refresh = jwtUtil.generateRefreshToken("ValidUser1", "user");
        String correctPassword = "{\"password\": \"V@lidP@$$w0rd\"}";

        mockMvc.perform(delete("/auth/self_service")
                .contentType(MediaType.APPLICATION_JSON)
                .content(correctPassword)
                .header("Authorization", "Bearer " + access)
                .header("Refresh", "Bearer " + refresh))
                .andExpect(status().isOk());

        assertThrows(EmptyResultDataAccessException.class ,() -> jdbc.queryForObject("SELECT id FROM user_crm WHERE given_name = 'John'", String.class));
        assertThrows(EmptyResultDataAccessException.class ,() -> jdbc.queryForObject("SELECT id FROM user_login WHERE user_name = 'ValidUser1'", String.class));
    }

    @Test
    void deleteWrongPassword() throws Exception {
        String access = jwtUtil.generateJwtToken("ValidUser1", "user");
        String refresh = jwtUtil.generateRefreshToken("ValidUser1", "user");
        String correctPassword = "{\"password\": \"V@lidP@$$w0rd1\"}";

        mockMvc.perform(delete("/auth/self_service")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(correctPassword)
                        .header("Authorization", "Bearer " + access)
                        .header("Refresh", "Bearer " + refresh))
                .andExpect(status().isForbidden());

        assertNotNull(jdbc.queryForObject("SELECT id FROM user_crm WHERE given_name = 'John'", String.class));
        assertNotNull(jdbc.queryForObject("SELECT id FROM user_login WHERE user_name = 'ValidUser1'", String.class));
    }
}
