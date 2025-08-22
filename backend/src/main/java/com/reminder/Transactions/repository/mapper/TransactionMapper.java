package com.reminder.Transactions.repository.mapper;

import com.reminder.Transactions.model.CategorySource;
import com.reminder.Transactions.model.Transaction;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionMapper implements RowMapper<Transaction> {
    @Override
    public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
        Transaction txn = new Transaction();
        txn.setId(rs.getLong("id"));
        txn.setUserId (rs.getLong("user_id"));
        txn.setTxnTime (rs.getTimestamp ("txn_time").toInstant());
        txn.setDescription (rs.getString ("description"));
        txn.setAmount (rs.getBigDecimal ("amount"));
        txn.setCategory (rs.getLong ("category_id"));
        txn.setCategorySource (CategorySource.valueOf(rs.getString("category_source")));
        txn.setUniqueWeight (rs.getInt ("unique_weight"));
        txn.setPaymentMethod (rs.getString ("payment_method"));
        txn.setComment (rs.getString ("comment"));
        return txn;
    }
}