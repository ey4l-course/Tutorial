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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest (webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class GetAllTxnIntegration {
    @Autowired
    JwtUtil jwt;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    TransactionRepository repo;

    private TokensDTO authenticateAs(String user, String role) {
        return new TokensDTO(
                "Bearer " + jwt.generateJwtToken(user, role),
                "Bearer " + jwt.generateRefreshToken(user, role)
        );
    }

    @Test
    void happyPathUser () throws Exception{
        TokensDTO dto = authenticateAs("benc", "user");

        MvcResult result = mockMvc.perform(get("/txn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("authorization", dto.getAccessToken())
                        .header("Refresh", dto.getRefreshToken()))
                .andExpect(status().isOk())
                .andReturn();

        String JSON = result.getResponse().getContentAsString();
        List<Transaction> responseList = mapper.readValue(JSON, new TypeReference<List<Transaction>>() {});
        List<Transaction> dataBaseList = repo.getTxnByUserId(1L);

        assertEquals(dataBaseList.size(), responseList.size());
    }

    @Test
    void userViolation () throws Exception{
        TokensDTO dto = authenticateAs("benc", "user");

        mockMvc.perform(get("/txn?user=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("authorization", dto.getAccessToken())
                        .header("Refresh", dto.getRefreshToken()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminPerUser () throws Exception{
        TokensDTO dto = authenticateAs("aliceg", "admin");

        MvcResult result = mockMvc.perform(get("/txn?user=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("authorization", dto.getAccessToken())
                        .header("Refresh", dto.getRefreshToken()))
                .andExpect(status().isOk())
                .andReturn();

        String JSON = result.getResponse().getContentAsString();
        List<Transaction> responseList = mapper.readValue(JSON, new TypeReference<List<Transaction>>() {});
        List<Transaction> dataBaseList = repo.getTxnByUserId(1L);

        assertEquals(dataBaseList.size(), responseList.size());
    }

    @Test
    void adminAll () throws Exception{
        TokensDTO dto = authenticateAs("aliceg", "admin");

        MvcResult result = mockMvc.perform(get("/txn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("authorization", dto.getAccessToken())
                        .header("Refresh", dto.getRefreshToken()))
                .andExpect(status().isOk())
                .andReturn();

        String JSON = result.getResponse().getContentAsString();
        List<Transaction> responseList = mapper.readValue(JSON, new TypeReference<List<Transaction>>() {});
        List<Transaction> dataBaseList = repo.getAllTransactions();

        assertEquals(dataBaseList.size(), responseList.size());
    }
}
