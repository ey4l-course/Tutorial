package com.reminder.Budget;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(OutputCaptureExtension.class)

public class TxnEndpointBasicTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    private final Map<String, Object> transactionJson = new HashMap<>();

    @Test
    void testWithValidTxn () throws Exception {
        Map<String, Object> transactionJson = new HashMap<>();
        transactionJson.put("description", "Rami Levi and more stuff toooo long");
        transactionJson.put("amount", new BigDecimal("45.90"));
        transactionJson.put("category", "Groceries");
        transactionJson.put("comment", "Weekly food supplies");

        String requestBody = objectMapper.writeValueAsString(transactionJson);

        mockMvc.perform(post("/txn")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testWithMissingParam () throws Exception {
        transactionJson.put("amount", new BigDecimal("45.90"));
        transactionJson.put("category", "Groceries");
        transactionJson.put("comment", "Weekly food supplies");

        String requestBody = objectMapper.writeValueAsString(transactionJson);

        mockMvc.perform(post("/txn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        transactionJson.put("description", "Rami Levi");
        transactionJson.remove("amount");

        mockMvc.perform(post("/txn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}
