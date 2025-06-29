package com.reminder.Budget.repository.mapper;

import com.reminder.Budget.model.Transaction;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionMapper implements RowMapper<Transaction> {
    @Override
    public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Transaction(
                rs.getInt("id"),
                rs.getDate("txn_time").toLocalDate(),
                rs.getString("description"),
                rs.getBigDecimal("amount"),
                rs.getString("category"),
                rs.getString("comment")
        );
    }
}
