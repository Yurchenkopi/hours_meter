INSERT INTO items (user_id, date, start_time, end_time, lunch_break, extra_hours_only, remark)
SELECT 3, date, start_time, end_time, lunch_break, false, '' FROM items_bak;