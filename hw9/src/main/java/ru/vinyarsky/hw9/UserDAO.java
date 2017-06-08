package ru.vinyarsky.hw9;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    final private static ExecutorHandler<User> handler = new Handler();

    public static long save(User user) throws DbException {
        Executor.save(user);
        return user.getId();
    }

    public static User load(long id) throws DbException {
        return Executor.load(User.class, handler, id);
    }

    private static class Handler implements ExecutorHandler<User> {
        @Override
        public User handle(ResultSet resultSet) throws DbException {
            try {
                if (!resultSet.next())
                    throw new DbException("Empty result set");

                User result = new User();
                result.id = resultSet.getLong(AnnotationHelper.getDbFieldName(User.class, "id"));
                result.name = resultSet.getString(AnnotationHelper.getDbFieldName(User.class, "name"));
                result.age = resultSet.getInt(AnnotationHelper.getDbFieldName(User.class, "age"));

                if (resultSet.next())
                    throw new DbException("Result set must contain one row only");

                return result;
            } catch (SQLException e) {
                throw new DbException("Error during handling of User result set", e);
            }
        }
    }
}
