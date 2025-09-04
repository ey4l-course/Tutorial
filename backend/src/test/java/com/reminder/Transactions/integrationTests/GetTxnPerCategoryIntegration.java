package com.reminder.Transactions.integrationTests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reminder.Transactions.model.Transaction;
import com.reminder.Transactions.repository.TransactionRepository;
import com.reminder.Users.model.TokensDTO;
import com.reminder.Users.utilities.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest (webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class GetTxnPerCategoryIntegration {
    @Autowired
    JwtUtil jwt;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    TransactionRepository repo;

    private TokensDTO authenticateAs(String user, String role) {
        return new TokensDTO(
                "Bearer " + jwt.generateJwtToken(user, role),
                "Bearer " + jwt.generateRefreshToken(user, role)
        );
    }

    @Test
    void HappyPathUser() throws Exception {
        TokensDTO dto = authenticateAs("benc", "user");

        MvcResult result = mockMvc.perform(get("/txn/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", dto.getAccessToken())
                        .header("Refresh", dto.getRefreshToken()))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        List<Transaction> responseList = mapper.readValue(responseJson, new TypeReference<List<Transaction>>() {});
        for (Transaction txn : responseList){
            assertEquals(2L, txn.getUserId());
        }
    }

    @Test
    void userUnauthorized () throws Exception {
        TokensDTO dto = authenticateAs("benc", "user");

        mockMvc.perform(get("/txn/1?user=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", dto.getAccessToken())
                        .header("Refresh", dto.getRefreshToken()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$", containsString("You are unauthorized to perform this operation")));
    }

    @Test
    void happyPathAdminPerUser () throws Exception {
        TokensDTO dto = authenticateAs("aliceg", "admin");

        MvcResult result = mockMvc.perform(get("/txn/1?user=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", dto.getAccessToken())
                        .header("Refresh", dto.getRefreshToken()))
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        List<Transaction> responseList = mapper.readValue(responseJson, new TypeReference<List<Transaction>>() {});

        List<Transaction> DbResult = repo.getUserTxnPerCategory(1L, 1L);
        assertEquals(DbResult.size(), responseList.size());
    }

    @Test
    void happyPathAdminAllTxn () throws Exception {
        TokensDTO dto = authenticateAs("aliceg", "admin");

        MvcResult result = mockMvc.perform(get("/txn/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", dto.getAccessToken())
                        .header("Refresh", dto.getRefreshToken()))
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        List<Transaction> responseList = mapper.readValue(responseJson, new TypeReference<List<Transaction>>() {});

        List<Transaction> DbResult = repo.getTxnPerCategory(1L);
        assertEquals(DbResult.size(), responseList.size());
    }
}
