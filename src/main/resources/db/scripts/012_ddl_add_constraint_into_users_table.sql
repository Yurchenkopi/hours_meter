ALTER TABLE users
ADD CONSTRAINT users_report_settings_key UNIQUE (report_settings_id);