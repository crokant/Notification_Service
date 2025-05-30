package com.icispp.notificationservice.repositories;

import com.icispp.notificationservice.models.Message;
import com.icispp.notificationservice.models.Subscription;
import com.icispp.notificationservice.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class SqlMessageRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Message save(Message message) {
        String sql = """
            INSERT INTO message (subject, content, sent_at, delivered, subscription_id, user_id)
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING id
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, message.getSubject());
            ps.setString(2, message.getContent());
            ps.setTimestamp(3, Timestamp.valueOf(message.getSentAt()));
            ps.setBoolean(4, message.getDelivered());
            ps.setLong(5, message.getSubscription().getId());
            ps.setLong(6, message.getUser().getId());
            return ps;
        }, keyHolder);

        message.setId(keyHolder.getKey().longValue());
        return message;
    }

    public List<Message> findAllByUser(User user) {
        String sql = "SELECT * FROM message WHERE user_id = ?";
        return jdbcTemplate.query(sql, new MessageRowMapper(), user.getId());
    }

    public List<Message> findAllBySubscription(Subscription subscription) {
        String sql = "SELECT * FROM message WHERE subscription_id = ?";
        return jdbcTemplate.query(sql, new MessageRowMapper(), subscription.getId());
    }

    public void updateDeliveryStatus(Long messageId, boolean status) {
        String sql = "UPDATE message SET delivered = ? WHERE id = ?";
        jdbcTemplate.update(sql, status, messageId);
    }

    public List<Message> findByDeliveredFalse() {
        String sql = "SELECT * FROM message WHERE delivered = false";
        return jdbcTemplate.query(sql, new MessageRowMapper());
    }

    public void deleteById(Long messageId) {
        String sql = "DELETE FROM message WHERE id = ?";
        jdbcTemplate.update(sql, messageId);
    }

    private static class MessageRowMapper implements RowMapper<Message> {
        @Override
        public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Message.builder()
                    .id(rs.getLong("id"))
                    .subject(rs.getString("subject"))
                    .content(rs.getString("content"))
                    .sentAt(rs.getTimestamp("sent_at").toLocalDateTime())
                    .delivered(rs.getBoolean("delivered"))
                    .build();
        }
    }
}