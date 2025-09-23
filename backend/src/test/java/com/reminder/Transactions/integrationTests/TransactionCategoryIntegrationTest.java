package com.reminder.Transactions.integrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reminder.Transactions.model.Transaction;
import com.reminder.Transactions.repository.TransactionRepository;
import com.reminder.Transactions.utilities.TxnUtility;
import com.reminder.Users.model.TokensDTO;
import com.reminder.Users.utilities.JwtUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
//@Sql(scripts = "/data.sql")
public class TransactionCategoryIntegrationTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    JwtUtil jwt;
    @Autowired
    TransactionRepository repo;
    @Autowired
    JdbcTemplate jdbc;

    private TokensDTO authenticateAs (String user, String role){
        return new  TokensDTO(
                "Bearer " + jwt.generateJwtToken(user, role),
                "Bearer " + jwt.generateRefreshToken(user, role)
        );
    }

    @Test
    void nonPermanentTxn () throws Exception {
        TokensDTO tokens = authenticateAs("benc", "user");
        Map<String, Object> dto = Map.of(
                "category", 3,
                "isPermanent", false
        );

        mockMvc.perform(patch("/txn/1/category")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", tokens.getAccessToken())
                .header("Refresh", tokens.getRefreshToken())
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        Transaction tstTxn = repo.getTxnById(1L);
        assertEquals(3L, tstTxn.getCategory());
        assertEquals(null, repo.userDefinedCategory(2L, "Shufersal"));
    }

    @Test
    void testUpdateExistingClassification() throws Exception {
        TokensDTO tokens = authenticateAs("benc", "user");
        Map<String, Object> dto = Map.of(
                "category", 2,  // fuel
                "isPermanent", true
        );

        mockMvc.perform(patch("/txn/2/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
                        .header("Authorization", tokens.getAccessToken())
                        .header("Refresh", tokens.getRefreshToken()))
                .andExpect(status().isOk());

        Transaction tstTxn = repo.getTxnById(2L);
        assertEquals(2L, tstTxn.getCategory());
        assertEquals(2L , repo.userDefinedCategory(2L, "PazGas"));
        assertEquals(1, repo.testCountEntries());
    }

    @Test
    void testUpdateNewClassification() throws Exception {
        TokensDTO tokens = authenticateAs("benc", "user");
        Map<String, Object> dto = Map.of(
                "category", 1,  // fuel
                "isPermanent", true
        );

        mockMvc.perform(patch("/txn/1/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
                        .header("Authorization", tokens.getAccessToken())
                        .header("Refresh", tokens.getRefreshToken()))
                .andExpect(status().isOk());

        Transaction tstTxn = repo.getTxnById(1L);
        assertEquals(1L, tstTxn.getCategory());
        assertEquals(1L , repo.userDefinedCategory(2L, "Shufersal"));
        assertEquals(2, repo.testCountEntries());
    }

    @Test
    void testUpdateByAdmin () throws Exception{
        TokensDTO tokens = authenticateAs("aliceg", "admin");
        Map<String, Object> dto = Map.of(
                "category", 1,
                "isPermanent", false
        );

        mockMvc.perform(patch("/txn/1/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
                        .header("Authorization", tokens.getAccessToken())
                        .header("Refresh", tokens.getRefreshToken()))
                .andExpect(status().isOk());
    }

}
