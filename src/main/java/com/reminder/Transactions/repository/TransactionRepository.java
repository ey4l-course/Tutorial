package com.reminder.Transactions.repository;

import com.reminder.Transactions.model.Transaction;
import com.reminder.Transactions.repository.mapper.TransactionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class TransactionRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${app.tables.transactions}")
    private String TABLE;

    public void save (Transaction transaction){
            KeyHolder keyHolder = new GeneratedKeyHolder();
            String sql = "INSERT INTO " + TABLE + " (txn_time, description, amount, category, comment) VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, new String[] {"id"});
                ps.setDate(1, java.sql.Date.valueOf(transaction.getTxnTime()));
                ps.setString(2, transaction.getDescription());
                ps.setBigDecimal(3, transaction.getAmount());
                ps.setString(4, transaction.getCategory());
                ps.setString(5, transaction.getComment());
                return ps;
            }, keyHolder);
    }

    public void addComment (int id, String comment){
        String sql = String.format("UPDATE %s SET comment = ? WHERE id = ?", TABLE);
        int updatedRows = jdbcTemplate.update(sql, comment, id);
        if (updatedRows == 0)
            throw new IllegalArgumentException(String.format("transaction with id %d not found", id));
    }

    public void changeCategory (int id, String category){
        String sql = String.format("UPDATE %s SET category = ? WHERE id = ?", TABLE);
        int updatedRows = jdbcTemplate.update(sql, category, id);
        if (updatedRows == 0)
            throw new IllegalArgumentException(String.format("transaction with id %d not found", id));
    }

    public List<Transaction> getTxnPerCategory (String category){
        final String sql = "SELECT * FROM " + TABLE + " WHERE category = ?";
        return jdbcTemplate.query(sql, new TransactionMapper(), category);
    }
    public List<Transaction> getAllTransactions () {
        final String sql = "SELECT * FROM " + TABLE;
        return jdbcTemplate.query(sql, new TransactionMapper());
    }
}
