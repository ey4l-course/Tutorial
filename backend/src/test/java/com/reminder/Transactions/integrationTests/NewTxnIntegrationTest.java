package com.reminder.Transactions.integrationTests;

import com.reminder.Transactions.repository.TransactionRepository;
import com.reminder.Users.utilities.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest (webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class NewTxnIntegrationTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    JwtUtil jwt;
    @Autowired
    JdbcTemplate jdbc;
    @Autowired
    TransactionRepository repo;

    @BeforeEach
    void setup () {
        jdbc.execute("DELETE FROM transactions_table");
        jdbc.execute("DELETE FROM user_transaction_classification");
        jdbc.execute("DELETE FROM global_transaction_classification");
        jdbc.execute("INSERT INTO user_transaction_classification (user_id, description, category_id, regular) VALUES (1, 'Amazon', 2, TRUE);");
        jdbc.execute("INSERT INTO global_transaction_classification (description, category_id, regular, vote_count) VALUES ('Starbucks', 5, TRUE, 100);");
    }

    @Test
    void ValidManualCategory () throws Exception {
        String access = "Bearer " + jwt.generateJwtToken("aliceg", "admin");
        String refresh = "Bearer " + jwt.generateRefreshToken("aliceg", "admin");
        String request = "  [\n" +
                "    {\n" +
                "      \"description\": \"Starbucks\",\n" +
                "      \"amount\": 12.50,\n" +
                "      \"category\": 5,\n" +
                "      \"paymentMethod\": \"CARD\",\n" +
                "      \"comment\": \"Morning coffee\"\n" +
                "    }\n" +
                "  ]";

        mockMvc.perform(post("/txn/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                        .header("Authorization", access)
                        .header("refresh", refresh))
                .andExpect(status().isOk());
    }

    @Test
    void emptyListRequest () throws Exception {
        String access = "Bearer " + jwt.generateJwtToken("aliceg", "admin");
        String refresh = "Bearer " + jwt.generateRefreshToken("aliceg", "admin");
        String request = "[]";

        mockMvc.perform(post("/txn/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                        .header("Authorization", access)
                        .header("refresh", refresh))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}
