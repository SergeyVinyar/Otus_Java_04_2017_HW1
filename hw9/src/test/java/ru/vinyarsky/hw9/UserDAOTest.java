package ru.vinyarsky.hw9;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.Assert.*;

/*

Для создания базы надо выполнить:

CREATE DATABASE hw9;

CREATE USER test_user WITH password 'test_user';

GRANT ALL ON DATABASE hw9 TO test_user;

 */

public class UserDAOTest {

    @org.junit.Before
    public void setUp() throws Exception {
        Connection connection = Database.getConnection();
        try(Statement statement = connection.createStatement()) {
            Executor.executeInTransaction(connection, () -> statement.execute("CREATE TABLE users (id SERIAL PRIMARY KEY, name VARCHAR(255), age NUMERIC(3))"));
        }
    }

    @org.junit.After
    public void tearDown() throws Exception {
        Connection connection = Database.getConnection();
        try(Statement statement = connection.createStatement()) {
            Executor.executeInTransaction(connection, () -> statement.execute("DROP TABLE users"));
        }
    }

    @org.junit.Test
    public void testUser() throws Exception {
        User user1 = new User();
        user1.name = "Сидоров Иван Дормидонтович";
        user1.age = 89;
        UserDAO.save(user1);

        assertEquals(1, user1.getId());

        User user2 = new User();
        user2.name = "Залипукина Агафья Петровна";
        user2.age = 33;
        UserDAO.save(user2);

        assertEquals(2, user2.getId());

        User user1loaded = UserDAO.load(1);
        assertEquals(user1.id, user1loaded.id);
        assertEquals(user1.name, user1loaded.name);
        assertEquals(user1.age, user1loaded.age);

        User user2loaded = UserDAO.load(2);
        assertEquals(user2.id, user2loaded.id);
        assertEquals(user2.name, user2loaded.name);
        assertEquals(user2.age, user2loaded.age);

        user2.name = "Попов Константин Игоревич";
        user2.age = 55;
        UserDAO.save(user2);

        assertEquals(2, user2.getId());

        user2loaded = UserDAO.load(2);
        assertEquals(user2.id, user2loaded.id);
        assertEquals(user2.name, user2loaded.name);
        assertEquals(user2.age, user2loaded.age);
    }
}