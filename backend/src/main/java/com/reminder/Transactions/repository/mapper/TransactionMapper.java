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
        txn.setUserId (rs.getLong("userId"));
        txn.setTxnTime (rs.getTimestamp ("txnTime").toInstant());
        txn.setDescription (rs.getString ("description"));
        txn.setAmount (rs.getBigDecimal ("amount"));
        txn.setCategory (rs.getLong ("category"));
        txn.setCategorySource (CategorySource.valueOf(rs.getString("categorySource")));
        txn.setUniqueWeight (rs.getInt ("uniqueWeight"));
        txn.setPaymentMethod (rs.getString ("paymentMethod"));
        txn.setComment (rs.getString ("comment"));
        txn.setRegular (rs.getBoolean ("isRegular"));
        return txn;
    }
}