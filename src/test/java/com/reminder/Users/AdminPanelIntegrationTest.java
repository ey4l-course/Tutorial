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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    void getAllProfiles () throws Exception{
        //correct admin access
        String access = jwtUtil.generateJwtToken("aliceg", "admin");
        String refresh = jwtUtil.generateRefreshToken("aliceg", "admin");

        mockMvc.perform(get("/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + access)
                .header("Refresh", "Bearer " + refresh))
                .andExpect(status().isOk());

        //Access without admin role
        access = jwtUtil.generateJwtToken("benc", "user");
        refresh = jwtUtil.generateRefreshToken("benc", "user");
        mockMvc.perform(get("/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + access)
                        .header("Refresh", "Bearer " + refresh))
                .andExpect(status().isForbidden());
    }

    @Test
    void searchProfileById() throws Exception {
        String access = jwtUtil.generateJwtToken("aliceg", "admin");
        String refresh = jwtUtil.generateRefreshToken("aliceg", "admin");

        //Search for user by ID OK
        mockMvc.perform(get("/admin/user/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + access)
                        .header("Refresh", "Bearer " + refresh))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.surname").value("Levi"));

        //Search for non-existing user by ID
        mockMvc.perform(get("/admin/user/33")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + access)
                        .header("Refresh", "Bearer " + refresh))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchProfilesByParamsOK() throws Exception {
        String access = jwtUtil.generateJwtToken("aliceg", "admin");
        String refresh = jwtUtil.generateRefreshToken("aliceg", "admin");

        //Search by given name
        mockMvc.perform(get("/admin/user?given-name=Ben")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + access)
                        .header("Refresh", "Bearer " + refresh))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0]").isNotEmpty());

        //Search by surname
        mockMvc.perform(get("/admin/user?surname=Katz")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + access)
                        .header("Refresh", "Bearer " + refresh))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0]").isNotEmpty());

        //Search By service level

        //Search by surname
        mockMvc.perform(get("/admin/user?service-level=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + access)
                        .header("Refresh", "Bearer " + refresh))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0]").isNotEmpty());
    }

    @Test
    void searchByBrokenParams () throws Exception{
        String access = jwtUtil.generateJwtToken("aliceg", "admin");
        String refresh = jwtUtil.generateRefreshToken("aliceg", "admin");

        mockMvc.perform(get("/admin/user?saervice-level=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + access)
                        .header("Refresh", "Bearer " + refresh))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Invalid search parameters"));
    }
}
