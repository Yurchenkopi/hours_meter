CREATE TABLE IF NOT EXISTS items (
  id SERIAL PRIMARY KEY,
  date date,
  start_time time,
  end_time time,
  lunch_break bool
);

--DROP TABLE items;

--DELETE FROM items
--WHERE date = '2023-06-11';