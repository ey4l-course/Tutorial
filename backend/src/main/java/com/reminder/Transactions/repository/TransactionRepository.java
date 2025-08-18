package com.reminder.Transactions.repository;

import com.reminder.Transactions.model.CategorySource;
import com.reminder.Transactions.model.ClassUpdateDTO;
import com.reminder.Transactions.model.Transaction;
import com.reminder.Transactions.repository.mapper.TransactionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository
public class TransactionRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${app.tables.transactions}")
    private String TABLE;

    @Value("${app.tables.userDefined}")
    private String USER_DEFINED;

    @Value("${app.tables.globalDefined}")
    private String GLOBAL_DEFINED;

    public void save (Transaction transaction){
            KeyHolder keyHolder = new GeneratedKeyHolder();
            String sql = "INSERT INTO " + TABLE + " (user_id, txn_time, description, amount, category_id, category_source, unique_weight, payment_method, comment)VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, new String[] {"id"});
                ps.setLong(1, transaction.getUserId());
                ps.setTimestamp(2, Timestamp.from(Instant.now()));
                ps.setString(3, transaction.getDescription());
                ps.setBigDecimal(4, transaction.getAmount());
                ps.setLong(5, transaction.getCategory());
                ps.setString(6, String.valueOf(transaction.getCategorySource()));
                ps.setInt(7, transaction.getUniqueWeight());
                ps.setString(8, transaction.getPaymentMethod());
                ps.setString(9, transaction.getComment());
                return ps;
            }, keyHolder);
    }

    public void addComment (Long id, String comment){
        String sql = String.format("UPDATE %s SET comment = ? WHERE id = ?", TABLE);
        jdbcTemplate.update(sql, comment, id);
    }

    public void changeCategory (Long id, Long category){
        String sql = String.format("UPDATE %s SET category = ?, category_source = ?, unique_weight = ?, WHERE id = ?", TABLE);
        int updatedRows = jdbcTemplate.update(sql, category, CategorySource.SPECIAL_CLASSIFICATION, 1, id);
        if (updatedRows == 0)
            throw new IllegalArgumentException(String.format("transaction with id %d not found", id));
    }

    public Long getUserIdByTxnId (Long txnId) throws DataAccessException {
        String sql = "SELECT user_id FROM " + TABLE + " WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, txnId);
    }

    public Transaction getTxnById (Long id){
        String sql = "SELECT * FROM " + TABLE + " WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new TransactionMapper(), id);
    }

    public List<Transaction> getTxnPerCategory (String category){
        final String sql = "SELECT * FROM " + TABLE + " WHERE category = ?";
        return jdbcTemplate.query(sql, new TransactionMapper(), category);
    }
    public List<Transaction> getAllTransactions () {
        final String sql = "SELECT * FROM " + TABLE;
        return jdbcTemplate.query(sql, new TransactionMapper());
    }

    public Long userDefinedCategory(Long userId, String description) {
        String sql = String.format("SELECT category FROM %s WHERE user_id = ? and description = ?", USER_DEFINED);
        try {
            return jdbcTemplate.queryForObject(sql, Long.class, userId, description);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Long globalDefinedCategory(String description) {
        String sql = String.format("SELECT category FROM %s WHERE description = ?", GLOBAL_DEFINED); //TODO: add 'and is_default = true' after finalizing table
        try {
            return jdbcTemplate.queryForObject(sql, Long.class, description);
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    public boolean isClassifiedByUser(ClassUpdateDTO dto) {
        String sql = "SELECT COUNT (*) FROM " + USER_DEFINED + " WHERE user_id = ?, and description = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, dto.getUserId(), dto.getDescription());
        if (count > 1)
            throw new IllegalStateException("Too many classifications for description " + dto.getDescription());
        return count == 1;
    }

    public String getTxnDesc(Long txnId) {
        String sql = "SELECT description FROM " + TABLE + " WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, String.class, txnId);
    }

    public void updateClassification(ClassUpdateDTO dto) {
        String sql = "UPDATE " + USER_DEFINED + " SET category = ? WHERE user_id = ? and description = ?";
        jdbcTemplate.update(sql, dto.getCategory(), dto.getUserId(), dto.getDescription());
    }

    public void firstClassification(ClassUpdateDTO dto) {
        KeyHolder key = new GeneratedKeyHolder();
        String sql = "INSERT INTO " + USER_DEFINED + " (user_id, description, category, is_regular) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new  String[] {"id"});
            ps.setLong(1, dto.getUserId());
            ps.setString(2, dto.getDescription());
            ps.setLong(3, dto.getCategory());
            ps.setBoolean(4, false);    //Possible future enhancement
            return ps;
        }, key);
    }
}
