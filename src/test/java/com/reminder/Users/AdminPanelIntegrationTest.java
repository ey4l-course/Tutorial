package com.reminder.Users;

import com.reminder.Users.repository.UsersRepository;
import com.reminder.Users.utilities.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AdminPanelIntegrationTest {
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

    @Test
    void getAllProfilesCorrect () throws Exception{
        String access = jwtUtil.generateJwtToken("aliceg", "admin");
        String refresh = jwtUtil.generateRefreshToken("aliceg", "admin");

        mockMvc.perform(get("/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + access)
                .header("Refresh", "Bearer " + refresh))
                .andExpect(status().isOk());
    }
}
