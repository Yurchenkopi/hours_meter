CREATE TABLE IF NOT EXISTS items (
                                     id SERIAL PRIMARY KEY,
                                     user_id INT REFERENCES users(id),
                                     date DATE,
                                     start_time TIME,
                                     end_time TIME,
                                     lunch_break BOOL,
                                     extra_hours_only BOOL,
                                     remark VARCHAR
);