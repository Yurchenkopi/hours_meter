CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       authority_id INT references authorities(id),
                       chat_id BIGINT,
                       username VARCHAR(50) NOT NULL DEFAULT 'user' UNIQUE,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       name VARCHAR (50),
                       patronymic VARCHAR (50),
                       surname VARCHAR (50),
                       password VARCHAR(100) NOT NULL,
                       enabled BOOLEAN DEFAULT TRUE
);