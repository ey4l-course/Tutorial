package com.reminder.Users;

import com.jayway.jsonpath.JsonPath;
import com.reminder.Users.model.IpAddress;
import com.reminder.Users.model.UserLogin;
import com.reminder.Users.repository.UsersRepository;
import com.reminder.Users.repository.mapper.IpAddressMapper;
import com.reminder.Users.utilities.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class RegistrationIntegrationTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    UsersRepository repo;
    @Autowired
    JdbcTemplate jdbc;
    @Autowired
    BCryptPasswordEncoder encoder;
    @Autowired
    JwtUtil jwtUtil;

    @BeforeEach
    void resetTestDb() {
        jdbc.execute("DELETE FROM common_ip");
        jdbc.execute("DELETE FROM user_login");
        jdbc.execute("DELETE FROM user_crm");
    }
    /*
    Valid username and password with valid IPv6 header
    Valid JWT with fully valid UserCRM
     */
    @Test
    void HappyPath() throws Exception {
        String IPv6 = "2001:4810:ed07:1317:0001:0000:0000:00ff";
        String body = "{\"userName\":\"ValidUser12\"," +
                "\"password\":\"V@lidPa$$w0rd\"}";

        mockMvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .with(request -> {
                    request.setRemoteAddr(IPv6);
                    return request;
                }))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("user created"))
                .andExpect(jsonPath("$.token").isNotEmpty());

        UserLogin savedUser = repo.getUserByUserName("ValidUser12");
        assertNotNull(savedUser);
        assertEquals("ValidUser12", savedUser.getUserName());
        assertTrue(encoder.matches("V@lidPa$$w0rd", savedUser.getHashedPassword()));
        assertFalse(savedUser.isActive());

        String sql = "SELECT * FROM common_ip";
        List<IpAddress> savedIp = jdbc.query(sql, new IpAddressMapper());
        assertNotNull(savedIp);
        assertEquals(savedIp.getFirst().getIpAddress(), "2001:4810:ed07:1317:1:0:0:ff");
        assertFalse(savedIp.getFirst().isSus());
    }

    /*
    Valid user name and password with valid IPv6 mapped IPv4 header
    Valid JWT with invalid UserCRM (violate data policy)

     */
    @Test
    void correctLoginWithInvalidUserDetails() throws Exception {
        String Ipv6mappedIPv4 = "::ffff:10.12.1.145";
        String loginBody = "{\"userName\":\"ValidUser12\"," +
                "\"password\":\"V@lidPa$$w0rd\"}";
        String jwt;
        String[] crmBody = {
                "{\"givenName\":\"J0hn\", \"surname\":\"Doe\", \"email\":\"johndoe@yahoo.com\", \"mobile\":\"12125551218\"}",
                "{\"givenName\":\"John\", \"surname\":\"Do$e\", \"email\":\"johndoe@yahoo.com\", \"mobile\":\"12125551218\"}",
                "{\"givenName\":\"John\", \"surname\":\"Doe\", \"email\":\"johndoe@yahoo.comma\", \"mobile\":\"12125551218\"}",
                "{\"givenName\":\"John\", \"surname\":\"Doe\", \"email\":\"johndoe@yahoo.com\", \"mobile\":\"+1-212-555-1218\"}",
                "{\"givenName\":\"\", \"surname\":\"Doe\", \"email\":\"johndoe@yahoo.com\", \"mobile\":\"12125551218\"}"
        };
        MvcResult result = mockMvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody)
                .with(request -> {
                    request.setRemoteAddr(Ipv6mappedIPv4);
                    return request;
                }))
                .andDo(print())
                .andReturn();
        jwt = JsonPath.read(result.getResponse().getContentAsString(), "$.token");

        for (String user : crmBody){
            mockMvc.perform(post("/auth/activate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(user)
                    .with(request -> {
                        request.addHeader("Authorization", "Bearer " + jwt);
                        return request;
                    }))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }
    }
    /*
    Valid username and password with valid IPv4 header
    Expired JWT
    */

    @Test
    void ValidLoginAndActivateWithExpiredJwt () throws Exception {
        JwtUtil jwtUtilTest = new JwtUtil("temporary-secret-key-for-dev-additional-length-to-get-to-256-bit", 1000L, 3000L);
        String IPv4 = "10.12.1.145";
        String loginBody = "{\"userName\":\"ValidUser12\",\"password\":\"V@lidPa$$w0rd\"}";
        String crmBody = "{\"givenName\":\"John\", \"surname\":\"Doe\", \"email\":\"johndoe@yahoo.com\", \"mobile\":\"12125551218\"}";

        String jwt = jwtUtilTest.generateJwtToken("ValidUser12", "user");
        System.out.println(jwt);
        Thread.sleep(2000);
        mockMvc.perform(post("/auth/activate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(crmBody)
                .with(request -> {
                    request.addHeader("Authorization", "Bearer " + jwt);
                    return request;
                }))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ValidLoginWithCorruptedToken () throws Exception {
        String crmBody = "{\"givenName\":\"John\", \"surname\":\"Doe\", \"email\":\"johndoe@yahoo.com\", \"mobile\":\"12125551218\"}";


        mockMvc.perform(post("/auth/activate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(crmBody)
                .with(request -> {
                    request.addHeader("Authorization", "Bearer Invalid JWT");
                    return request;
                }))
                .andExpect(status().isForbidden());
    }
}
