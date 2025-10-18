ALTER TABLE employers_employees
    ADD CONSTRAINT employers_employees_unique_key UNIQUE (employers_id, employees_id);