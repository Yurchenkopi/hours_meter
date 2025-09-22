CREATE TABLE IF NOT EXISTS employers_employees (
                                     id SERIAL PRIMARY KEY,
                                     employers_id INT REFERENCES users(id),
                                     employees_id INT REFERENCES users(id)
);