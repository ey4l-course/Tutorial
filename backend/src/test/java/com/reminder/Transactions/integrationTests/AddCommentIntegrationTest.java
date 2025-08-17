package com.reminder.Transactions.integrationTests;

import com.reminder.Users.utilities.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AddCommentIntegrationTest {
    @Autowired
    JdbcTemplate db;
    @Autowired
    JwtUtil jwt;
    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void testSetup (){
        Timestamp time = Timestamp.from(Instant.now());
            db.execute("TRUNCATE TABLE transactions_table");
            db.execute("TRUNCATE TABLE user_transaction_classification");
            db.execute("TRUNCATE TABLE global_transaction_classification");
        db.execute("INSERT INTO transactions_table (user_id, txn_time, description, amount, category_id, payment_method) VALUES " +
                "(1, '" + time + "', 'Rami Levi', 100, 2, 'credit')," +
                "(2, '" + time + "', 'Supersal', 200, 2, 'credit')");
    }

    @Test
    void addCommentByUserSuccess () throws Exception {
        String access = "Bearer " + jwt.generateJwtToken("benc", "user");
        String refresh = "Bearer " + jwt.generateRefreshToken("benc", "user");

        mockMvc.perform(patch("/txn/2/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("Groceries for holidays")
                        .header("Authorization", access)
                        .header("Refresh", refresh))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void addCommentByUserEmpty () throws Exception{
        String access = "Bearer " + jwt.generateJwtToken("benc", "user");
        String refresh = "Bearer " + jwt.generateRefreshToken("benc", "user");

        mockMvc.perform(patch("/txn/2/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", access)
                        .header("Refresh", refresh))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    void addCommentByUserInvalid () throws Exception {
        String access = "Bearer " + jwt.generateJwtToken("benc", "user");
        String refresh = "Bearer " + jwt.generateRefreshToken("benc", "user");

        mockMvc.perform(patch("/txn/2/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("onetwothreefourfivesixseveneightninetenonetwothreefourfivesixseveneightninetenonetwothreefourfivesixseveneightnineten1234567890")
                        .header("Authorization", access)
                        .header("Refresh", refresh))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addCommentByUserForbidden () throws Exception{
        String access = "Bearer " + jwt.generateJwtToken("benc", "user");
        String refresh = "Bearer " + jwt.generateRefreshToken("benc", "user");

        mockMvc.perform(patch("/txn/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("Valid comment")
                        .header("Authorization", access)
                        .header("Refresh", refresh))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    void addCommentByAdmin () throws Exception {
        String access = "Bearer " + jwt.generateJwtToken("aliceg", "admin");
        String refresh = "Bearer " + jwt.generateRefreshToken("aliceg", "admin");

        mockMvc.perform(patch("/txn/2/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("Valid comment")
                        .header("Authorization", access)
                        .header("Refresh", refresh))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
