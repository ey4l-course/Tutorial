package com.reminder.Users.repository.mapper;

import com.reminder.Users.model.IpAddress;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class IpAddressMapper implements RowMapper<IpAddress> {
    @Override
    public IpAddress mapRow(ResultSet rs, int rowNum) throws SQLException {
        IpAddress ipAddress = new IpAddress();
        ipAddress.setId(rs.getLong("id"));
        ipAddress.setUserId(rs.getLong("user_id"));
        ipAddress.setIpAddress(rs.getString("ip_address"));
        ipAddress.setLastSeen(rs.getTimestamp("last_seen").toInstant());
        ipAddress.setUsageCount(rs.getInt("usage_count"));
        ipAddress.setSus(rs.getBoolean("is_sus"));
        return ipAddress;
    }

    /*
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    last_seen TIMESTAMP NOT NULL,
    usage_count INT DEFAULT 1,
    is_sus BOOLEAN DEFAULT FALSE,
     */
}
