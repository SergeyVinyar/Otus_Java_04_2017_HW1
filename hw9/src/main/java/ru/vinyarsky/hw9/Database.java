package ru.vinyarsky.hw9;

import java.sql.*;

/* package */ class Database {

    private static Connection connection;

    private static void init() throws SQLException {
        DriverManager.registerDriver(new org.postgresql.Driver());
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/hw9", "test_user", "test_user");
        connection.setAutoCommit(false);
    }

    public static Connection getConnection() throws SQLException {
        init();
        return connection;
    }
}
