package com.icispp.notificationservice.repositories;

import com.icispp.notificationservice.models.Subscription;
import com.icispp.notificationservice.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class SqlSubscriptionRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final RowMapper<Subscription> SUBSCRIPTION_ROW_MAPPER = (rs, rowNum) ->
            Subscription.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .creator(User.builder().id(rs.getLong("creator_id")).build())
                    .build();

    public Subscription createSubscription(Long creatorId, Subscription subscription) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO subscription (name, creator_id) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, subscription.getName());
            ps.setLong(2, creatorId);
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            subscription.setId(keyHolder.getKey().longValue());
        }
        return subscription;
    }

    public Optional<Subscription> findSubscriptionById(Long id) {
        Subscription subscription = jdbcTemplate.queryForObject(
                "SELECT id, name, creator_id FROM subscription WHERE id = ?",
                SUBSCRIPTION_ROW_MAPPER,
                id
        );
        return Optional.ofNullable(subscription);
    }

    public void deleteSubscription(Long subscriptionId) {
        jdbcTemplate.update("DELETE FROM subscription WHERE id = ?", subscriptionId);
    }

    public void addSubscriptionToUser(Long userId, Long subscriptionId) {
        jdbcTemplate.update(
                "INSERT INTO user_subscription (user_id, subscription_id) VALUES (?, ?)",
                userId,
                subscriptionId
        );

    }

    public void removeSubscriptionFromUser(Long userId, Long subscriptionId) {
        jdbcTemplate.update(
                "DELETE FROM user_subscription WHERE user_id = ? AND subscription_id = ?",
                userId,
                subscriptionId
        );
    }


    public Set<Subscription> getUserSubscriptions(Long userId) {
        List<Subscription> subscriptions = jdbcTemplate.query(
                "SELECT s.id, s.name, s.creator_id " +
                        "FROM subscription s " +
                        "JOIN user_subscription us ON s.id = us.subscription_id " +
                        "WHERE us.user_id = ?",
                SUBSCRIPTION_ROW_MAPPER,
                userId
        );
        return new HashSet<>(subscriptions);
    }

    public Set<Subscription> getCreatedSubscriptions(Long creatorId) {
        List<Subscription> subscriptions = jdbcTemplate.query(
                "SELECT id, name, creator_id FROM subscription WHERE creator_id = ?",
                SUBSCRIPTION_ROW_MAPPER,
                creatorId
        );
        return new HashSet<>(subscriptions);
    }


    public Subscription updateSubscription(Subscription subscription) {
        jdbcTemplate.update(
                "UPDATE subscription SET name = ?, creator_id = ? WHERE id = ?",
                subscription.getName(),
                subscription.getCreator().getId(),
                subscription.getId()
        );
        return subscription;
    }

    public boolean subscriptionExistsForUser(Long userId, Long subscriptionId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_subscription " +
                        "WHERE user_id = ? AND subscription_id = ?",
                Integer.class,
                userId,
                subscriptionId
        );
        return count != null && count > 0;
    }
}