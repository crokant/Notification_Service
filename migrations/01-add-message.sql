-- Создание таблицы сообщений
CREATE TABLE IF NOT EXISTS message (
       id BIGSERIAL PRIMARY KEY,
       subject VARCHAR(255),
       content TEXT,
       sent_at TIMESTAMP NOT NULL,
       delivered BOOLEAN,
       subscription_id BIGINT NOT NULL,
       user_id BIGINT NOT NULL,
       CONSTRAINT fk_message_subscription
           FOREIGN KEY (subscription_id)
               REFERENCES subscription(id)
               ON DELETE CASCADE,
       CONSTRAINT fk_message_user
           FOREIGN KEY (user_id)
               REFERENCES app_user(id)
               ON DELETE CASCADE
);