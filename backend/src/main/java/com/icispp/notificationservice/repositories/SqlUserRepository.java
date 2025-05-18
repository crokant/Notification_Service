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

@Repository
public class SqlUserRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) ->
            User.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .surname(rs.getString("surname"))
                    .email(rs.getString("email"))
                    .phoneNumber(rs.getString("phone_number"))
                    .password(rs.getString("password"))
                    .build();

    public User saveUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO app_user (name, surname, email, phone_number, password) " +
                            "VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getName());
            ps.setString(2, user.getSurname());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhoneNumber());
            ps.setString(5, user.getPassword());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            user.setId(keyHolder.getKey().longValue());
        }
        return user;
    }

    public Optional<User> findById(Long id) {
        User user = jdbcTemplate.queryForObject(
                "SELECT id, name, surname, email, phone_number, password " +
                        "FROM app_user WHERE id = ?",
                USER_ROW_MAPPER,
                id
        );
        if (user != null) {
            loadSubscriptions(user);
        }
        return Optional.ofNullable(user);
    }

    public List<User> findAll() {
        List<User> users = jdbcTemplate.query(
                "SELECT id, name, surname, email, phone_number, password FROM app_user",
                USER_ROW_MAPPER
        );
        users.forEach(this::loadSubscriptions);
        return users;
    }

    public User updateUser(User user) {
        jdbcTemplate.update(
                "UPDATE app_user SET name = ?, surname = ?, email = ?, " +
                        "phone_number = ?, password = ? WHERE id = ?",
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getPassword(),
                user.getId()
        );
        return user;
    }

    public void deleteUser(Long id) {
        jdbcTemplate.update("DELETE FROM app_user WHERE id = ?", id);
    }

    // Поиск по email
    public Optional<User> findByEmail(String email) {
        User user = jdbcTemplate.queryForObject(
                "SELECT id, name, surname, email, phone_number, password " +
                        "FROM app_user WHERE email = ?",
                USER_ROW_MAPPER,
                email
        );
        if (user != null) {
            loadSubscriptions(user);
        }
        return Optional.ofNullable(user);
    }

    public Optional<User> findByUsername(String username) {
        try {
            User user = jdbcTemplate.queryForObject(
                    "SELECT id, name, surname, email, phone_number, password " +
                            "FROM app_user WHERE name = ?",
                    USER_ROW_MAPPER,
                    username
            );

            if (user != null) {
                loadSubscriptions(user);
            }
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public boolean existsByName(String name) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM app_user WHERE name = ?",
                Integer.class,
                name
        );
        return count != null && count > 0;
    }

    public boolean existsByEmail(String email) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM app_user WHERE email = ?",
                Integer.class,
                email
        );
        return count != null && count > 0;
    }

    // Добавление подписки
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

    private void loadSubscriptions(User user) {
        List<Long> subscriptionIds = jdbcTemplate.query(
                "SELECT subscription_id FROM user_subscription WHERE user_id = ?",
                (rs, rowNum) -> rs.getLong("subscription_id"),
                user.getId()
        );
        user.setSubscriptions(new HashSet<>(subscriptionIds.stream()
                .map(id -> Subscription.builder().id(id).build())
                .toList()));

    }
}