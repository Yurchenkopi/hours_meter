DELETE FROM report_settings;

INSERT INTO report_settings (id, date_column, start_time_column, end_time_column, lunch_break_column, extra_hours_only_column, remark_column, hours_column)
SELECT
    u.id,
    false,
    false,
    false,
    false,
    false,
    false,
    false
FROM users u
WHERE u.report_settings_id IS NULL;