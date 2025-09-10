ALTER table users
ADD COLUMN report_settings_id int REFERENCES report_settings(id);