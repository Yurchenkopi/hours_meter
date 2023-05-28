CREATE TABLE IF NOT EXISTS items (
  id SERIAL PRIMARY KEY,
  date date,
  start_time time,
  end_time time
);

DROP TABLE items;