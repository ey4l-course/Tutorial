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

import java.util.ArrayList;
import java.util.List;

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

    @Test
    void updateProfileByAdmin () throws Exception {
        String access = jwtUtil.generateJwtToken("aliceg", "admin");
        String refresh = jwtUtil.generateRefreshToken("aliceg", "admin");
        String givenName = "\"givenName\":\"Benjamin\"";
        String surname = "\"surname\":\"Katz\"";
        String email = "\"email\":\"new@mail.com\"";
        String mobile = "\"mobile\":\"0527654321\"";
        String serviceLevel = "\"serviceLevel\":\"2\"";
//Test with full profile
        mockMvc.perform(patch("/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + access)
                        .header("Refresh", "Bearer " + refresh)
                        .content("{" + givenName + ", " +
                                surname + ", " +
                                email + ", " +
                                mobile + ", " +
                                serviceLevel + "}"))
                .andExpect(status().isAccepted());
//Test without givenName
        mockMvc.perform(patch("/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + access)
                        .header("Refresh", "Bearer " + refresh)
                        .content("{" + surname + ", " +
                                email + ", " +
                                mobile + ", " +
                                serviceLevel + "}"))
                .andExpect(status().isAccepted());
//Test without surname
        mockMvc.perform(patch("/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + access)
                        .header("Refresh", "Bearer " + refresh)
                        .content("{" + givenName + ", " +
                                email + ", " +
                                mobile + ", " +
                                serviceLevel + "}"))
                .andExpect(status().isAccepted());
//Test without email
        mockMvc.perform(patch("/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + access)
                        .header("Refresh", "Bearer " + refresh)
                        .content("{" + givenName + ", " +
                                surname + ", " +
                                mobile + ", " +
                                serviceLevel + "}"))
                .andExpect(status().isAccepted());
//Test without mobile
        mockMvc.perform(patch("/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + access)
                        .header("Refresh", "Bearer " + refresh)
                        .content("{" + givenName + ", " +
                                surname + ", " +
                                email + ", " +
                                serviceLevel + "}"))
                .andExpect(status().isAccepted());
    }


    @Test
        void updateProfileByAdminWithInvalidDetails () throws Exception {
        String access = jwtUtil.generateJwtToken("aliceg", "admin");
        String refresh = jwtUtil.generateRefreshToken("aliceg", "admin");
        List<String> testString = new ArrayList<>();
        testString.add ("\"givenName\":\"Be1njamin\"");
        testString.add ("\"surname\":\"Kat3z\"");
        testString.add ("\"email\":\"new@mail.comma\"");
        testString.add ("\"mobile\":\"052-7654321\"");
        testString.add ("");

        for (String field : testString) {
            mockMvc.perform(patch("/admin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + access)
                            .header("Refresh", "Bearer " + refresh)
                            .content("{" + field + "}"))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Test
    void updateProfileByAdminEmptyBody () throws Exception {
        String access = jwtUtil.generateJwtToken("aliceg", "admin");
        String refresh = jwtUtil.generateRefreshToken("aliceg", "admin");

        mockMvc.perform(patch("/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + access)
                        .header("Refresh", "Bearer " + refresh))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void activationCorrect () throws Exception{
        String access = jwtUtil.generateJwtToken("aliceg", "admin");
        String refresh = jwtUtil.generateRefreshToken("aliceg", "admin");

        mockMvc.perform(patch("/admin/activation/2/false")
                .header("Authorization", "Bearer " + access)
                .header("Refresh", "Bearer " + refresh))
            .andExpect(status().isAccepted());


        mockMvc.perform(patch("/admin/activation/3/true")
                        .header("Authorization", "Bearer " + access)
                        .header("Refresh", "Bearer " + refresh))
                .andExpect(status().isAccepted());
    }

    @Test
    void activationConflict () throws Exception{
        String access = jwtUtil.generateJwtToken("aliceg", "admin");
        String refresh = jwtUtil.generateRefreshToken("aliceg", "admin");

        mockMvc.perform(patch("/admin/activation/3/false")
                        .header("Authorization", "Bearer " + access)
                        .header("Refresh", "Bearer " + refresh))
                .andExpect(status().isConflict());


        mockMvc.perform(patch("/admin/activation/2/true")
                        .header("Authorization", "Bearer " + access)
                        .header("Refresh", "Bearer " + refresh))
                .andExpect(status().isConflict());
    }

    @Test
    void createSpecialUserSuccess () throws Exception{
        String access = jwtUtil.generateJwtToken("aliceg", "admin");
        String refresh = jwtUtil.generateRefreshToken("aliceg", "admin");
        String requestBody = "{\n" +
                "        \"userName\":\"JoeC241\",\n" +
                "        \"password\":\"InitialP@ssw0rd\",\n" +
                "        \"role\":\"admin\",\n" +
                "        \"isActive\":\"true\"\n" +
                "        }";

        mockMvc.perform(put("/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer " + access)
                        .header("Refresh", "Bearer " + refresh))
                .andExpect(status().isCreated())
                .andDo(print());
    }
    @Test
    void createSpecialUserUnlawful() throws Exception{
        String access = jwtUtil.generateJwtToken("aliceg", "admin");
        String refresh = jwtUtil.generateRefreshToken("aliceg", "admin");
        String requestBody = "{\n" +
                "        \"userName\":\"JoeC241\",\n" +
                "        \"password\":\"InitialP@ssw0rd\",\n" +
                "        \"role\":\"user\",\n" +
                "        \"isActive\":\"true\"\n" +
                "        }";

        mockMvc.perform(put("/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer " + access)
                        .header("Refresh", "Bearer " + refresh))
                .andExpect(status().isForbidden());
    }

    @Test
    void createSpecialUserExistingUser() throws Exception{
        String access = jwtUtil.generateJwtToken("aliceg", "admin");
        String refresh = jwtUtil.generateRefreshToken("aliceg", "admin");
        String requestBody = "{\n" +
                "        \"userName\":\"aliceg\",\n" +
                "        \"password\":\"InitialP@ssw0rd\",\n" +
                "        \"role\":\"admin\",\n" +
                "        \"isActive\":\"true\"\n" +
                "        }";

        mockMvc.perform(put("/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer " + access)
                        .header("Refresh", "Bearer " + refresh))
                .andExpect(status().isBadRequest());
    }
    @Test
    void createSpecialUserInvalidCredential() throws Exception{
        String access = jwtUtil.generateJwtToken("aliceg", "admin");
        String refresh = jwtUtil.generateRefreshToken("aliceg", "admin");
        String requestBody = "{\n" +
                "        \"userName\":\"alic  eg\",\n" +
                "        \"password\":\"InitialP@ssw0rd\",\n" +
                "        \"role\":\"admin\",\n" +
                "        \"isActive\":\"true\"\n" +
                "        }";

        mockMvc.perform(put("/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer " + access)
                        .header("Refresh", "Bearer " + refresh))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    void deleteUser () throws Exception {
        String access = jwtUtil.generateJwtToken("aliceg", "admin");
        String refresh = jwtUtil.generateRefreshToken("aliceg", "admin");
        String requestBody = "{\"id\":2, \"userName\":\"aliceg\", \"password\":\"HASHED_1\"}";

        mockMvc.perform(delete("/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer " + access)
                        .header("Refresh", "Bearer " + refresh))
                .andExpect(status().isOk());
        mockMvc.perform(get("/admin/user?given-name=Ben")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer " + access)
                        .header("Refresh", "Bearer " + refresh))
                .andExpect(jsonPath("$").isEmpty())
                .andDo(print());
    }
    @Test
    void deleteUserNotFound () throws Exception {
        String access = jwtUtil.generateJwtToken("aliceg", "admin");
        String refresh = jwtUtil.generateRefreshToken("aliceg", "admin");
        String requestBody = "{\"id\":12, \"userName\":\"aliceg\", \"password\":\"HASHED_1\"}";

        mockMvc.perform(delete("/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer " + access)
                        .header("Refresh", "Bearer " + refresh))
                .andExpect(status().isNotFound())
                .andDo(print());
        mockMvc.perform(get("/admin/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer " + access)
                        .header("Refresh", "Bearer " + refresh))
                .andExpect(jsonPath("$").isNotEmpty())
                .andDo(print());
    }

    @Test
    void searchUser () throws Exception {
        String access = jwtUtil.generateJwtToken("aliceg", "admin");
        String refresh = jwtUtil.generateRefreshToken("aliceg", "admin");

        mockMvc.perform(get("/admin/user?active=true&&role=user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + access)
                        .header("Refresh", "Bearer " + refresh))
                .andExpect(status().isOk())
                .andDo(print());
        mockMvc.perform(get("/admin/user?active=true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + access)
                        .header("Refresh", "Bearer " + refresh))
                .andExpect(status().isOk())
                .andDo(print());
        mockMvc.perform(get("/admin/user?role=user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + access)
                        .header("Refresh", "Bearer " + refresh))
                .andExpect(status().isOk())
                .andDo(print());
    }

}