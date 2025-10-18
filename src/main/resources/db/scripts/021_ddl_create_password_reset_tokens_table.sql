CREATE TABLE IF NOT EXISTS password_reset_tokens (
                                       id SERIAL PRIMARY KEY,
                                       token VARCHAR(255) NOT NULL,
                                       expiry_date TIMESTAMP NOT NULL,
                                       user_id INT REFERENCES users(id)
);