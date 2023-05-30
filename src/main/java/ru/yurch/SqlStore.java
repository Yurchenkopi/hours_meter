package ru.yurch;

import java.io.InputStream;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Predicate;

public class SqlStore implements Store, AutoCloseable {

    private Connection cn;

    public SqlStore(Connection cn) {
        this.cn = cn;
    }

    public SqlStore() {
    }

    public void init() {
        ClassLoader loader = SqlStore.class.getClassLoader();
        try (InputStream in = loader.getResourceAsStream("app.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver-class-name"));
            cn = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void close() throws Exception {
        if (cn != null) {
            cn.close();
        }
    }

    private Item rslSetToItem(ResultSet rslSet) throws SQLException {
        return new Item(
                rslSet.getInt("id"),
                rslSet.getDate("date").toLocalDate(),
                rslSet.getTime("start_time").toLocalTime(),
                rslSet.getTime("end_time").toLocalTime(),
                rslSet.getBoolean("lunch_break")
        );
    }

    @Override
    public Item add(Item item) {
        try (var ps = cn.prepareStatement(
                "INSERT INTO items(date, start_time, end_time, lunch_break) VALUES (?, ?, ?, ?);",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, Date.valueOf(item.getDate()));
            ps.setTime(2, Time.valueOf(item.getStartTime()));
            ps.setTime(3, Time.valueOf(item.getEndTime()));
            ps.setBoolean(4, item.isLunchBreak());
            ps.execute();
            try (var rslSet = ps.getGeneratedKeys()) {
                if (rslSet.next()) {
                    item.setId(rslSet.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return item;
    }

    @Override
    public List<Item> findByDate(LocalDate startDate, LocalDate endDate) {
        List<Item> data = new ArrayList<>();
        try (var ps = cn.prepareStatement(
                "SELECT * FROM items WHERE date BETWEEN ? AND ? ;")) {
            ps.setDate(1, Date.valueOf(startDate));
            ps.setDate(2, Date.valueOf(endDate));
            try (var rslSet = ps.executeQuery()) {
                while (rslSet.next()) {
                    data.add(rslSetToItem(rslSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
}
