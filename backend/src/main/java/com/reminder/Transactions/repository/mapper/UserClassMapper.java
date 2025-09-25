package com.reminder.Transactions.repository.mapper;

import com.reminder.Transactions.model.UserClassification;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserClassMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserClassification classification = new UserClassification();
        classification.setCategory(rs.getLong("category"));
        return null;
    }
}
