-- schema.sql

-- User Table
CREATE TABLE IF NOT EXISTS user (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      email VARCHAR(255) UNIQUE NOT NULL,
                      password VARCHAR(255),
                      role VARCHAR(255),
                      first_name VARCHAR(255),
                      last_name VARCHAR(255),
                      profile_picture MEDIUMBLOB,
                      file_type VARCHAR(255),
                      picture_url VARCHAR(255),
                      phone_number VARCHAR(255),
                      enabled BOOLEAN DEFAULT FALSE
);

-- ConfirmationToken Table
CREATE TABLE IF NOT EXISTS confirmation_token (
                                    token_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    confirmation_token INT,
                                    created_date TIMESTAMP,
                                    user_id BIGINT,
                                    FOREIGN KEY (user_id) REFERENCES user (id)
);
