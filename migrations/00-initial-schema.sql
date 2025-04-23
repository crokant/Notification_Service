-- Создание таблицы пользователей
CREATE TABLE IF NOT EXISTS app_user (
      id BIGSERIAL PRIMARY KEY,
      name VARCHAR(255) NOT NULL,
      surname VARCHAR(255),
      email VARCHAR(255) UNIQUE NOT NULL,
      phone_number VARCHAR(20),
      password VARCHAR(255) NOT NULL
);

-- Создание таблицы подписок
CREATE TABLE IF NOT EXISTS subscription (
      id BIGSERIAL PRIMARY KEY,
      name VARCHAR(255) NOT NULL,
      creator_id BIGINT NOT NULL,
      CONSTRAINT fk_creator
          FOREIGN KEY (creator_id)
              REFERENCES app_user(id)
              ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS user_subscription (
       user_id BIGINT NOT NULL,
       subscription_id BIGINT NOT NULL,
       PRIMARY KEY (user_id, subscription_id),
       CONSTRAINT fk_user
           FOREIGN KEY (user_id)
               REFERENCES app_user(id)
               ON DELETE CASCADE,
       CONSTRAINT fk_subscription
           FOREIGN KEY (subscription_id)
               REFERENCES subscription(id)
               ON DELETE CASCADE
);

-- Индексы для оптимизации
CREATE INDEX idx_user_email ON app_user(email);
CREATE INDEX idx_subscription_creator ON subscription(creator_id);