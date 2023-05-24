package ru.yurch;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.function.Predicate;

public class SqlStore implements Store, AutoCloseable {

    private Connection cn;

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
                rslSet.getTimestamp("date").toLocalDateTime(),
                rslSet.getTime("start_time").toLocalTime(),
                rslSet.getTime("end_time").toLocalTime()
        );
    }

    @Override
    public void add(Item item) {

    }

    @Override
    public List<Item> findBy(Predicate<Item> condition) {
        return null;
    }
}
