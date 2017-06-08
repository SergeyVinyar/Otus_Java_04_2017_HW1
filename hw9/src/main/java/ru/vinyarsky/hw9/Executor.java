package ru.vinyarsky.hw9;

import javafx.beans.binding.ListBinding;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.stream.*;

/* package */ class Executor {

    public static <T extends DataSet> void save(T dataSet) throws DbException {
        Class<?> clazz = dataSet.getClass();
        try {
            Connection connection = Database.getConnection();

            boolean exists = dataSet.getId() != 0;

            List<Field> fields = Arrays.stream(clazz.getDeclaredFields())
                    .filter(field -> field.getDeclaredAnnotation(Transient.class) == null)
                    .filter(field -> field.getDeclaredAnnotation(Id.class) == null)
                    .collect(Collectors.toList());

            if (!exists) {
                // INSERT INTO TableName (FieldName1, FieldName2, ...) VALUES (?, ?, ...)
                String sqlQuery = "INSERT INTO " + AnnotationHelper.getDbTableName(clazz) + " (" +
                        fields.stream().map(AnnotationHelper::getDbFieldName).collect(Collectors.joining(", ")) +
                        ") VALUES (" +
                        getMultipliedPattern((int) fields.size(), "?").collect(Collectors.joining(", ")) +
                        ")";

                try (PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
                    populateStatementWithValues(statement, fields, dataSet);
                    executeInTransaction(connection, statement::executeUpdate);
                    dataSet.id = getGeneratedId(statement);
                }
            } else {
                // UPDATE TableName SET FieldName1 = ?, FieldName2 = ?, ... WHERE Id = ...
                String sqlQuery = "UPDATE " + AnnotationHelper.getDbTableName(clazz) + " SET " +
                        fields.stream().map(AnnotationHelper::getDbFieldName).map(fieldName -> fieldName + " = ?").collect(Collectors.joining(", ")) +
                        " WHERE " + AnnotationHelper.getDbPrimaryKeyFieldName(clazz) + " = " + dataSet.getId();

                try (PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
                    populateStatementWithValues(statement, fields, dataSet);
                    executeInTransaction(connection, statement::executeUpdate);
                }
            }
        } catch (SQLException e) {
            throw new DbException("Error while saving", e);
        }
    }

    public static <T extends DataSet> T load(Class<T> clazz, ExecutorHandler<T> handler, long id) throws DbException {
        try {
            Connection connection = Database.getConnection();
            try {
                T result;
                try(Statement statement = connection.createStatement()) {
                    String sqlQuery = String.format("SELECT * FROM %s WHERE %s = %d", AnnotationHelper.getDbTableName(clazz), AnnotationHelper.getDbPrimaryKeyFieldName(clazz), id);
                    if (statement.execute(sqlQuery)) {
                        result = handler.handle(statement.getResultSet());
                    } else {
                        result = null;
                    }
                }
                // ??? Select-выражение почему-то тоже открывает транзакцию и блокирует таблицу
                connection.commit();
                return result;
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DbException("Error while loading User with Id = " + id, e);
        }
    }

    private static Stream<String> getMultipliedPattern(int count, String pattern) {
        String[] array = new String[count];
        Arrays.fill(array, pattern);
        return Arrays.stream(array);
    }

    private static void populateStatementWithValues(PreparedStatement statement, List<Field> fields, DataSet dataSet) throws SQLException {
        int index = 1;
        for (Field field : fields) {
            boolean accessible = field.isAccessible();
            if (!accessible)
                field.setAccessible(true);
            try {
                statement.setObject(index, field.get(dataSet));
            } catch (IllegalAccessException e) {
                // Мы сюда не попадем
            }
            if (!accessible)
                field.setAccessible(false);
            index++;
        }
    }

    private static long getGeneratedId(Statement statement) throws SQLException, DbException {
        try(ResultSet keys = statement.getGeneratedKeys()) {
            if (keys != null && keys.next())
                return keys.getLong(1);
            throw new DbException("No generated key");
        }
    }

    public static void executeInTransaction(Connection connection, RunnableWithSQLException runnable) throws SQLException {
        try {
            runnable.run();
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            throw e;
        }
    }

    @FunctionalInterface
    public interface RunnableWithSQLException {
        void run() throws SQLException;
    }
}
