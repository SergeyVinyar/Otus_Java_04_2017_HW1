package ru.vinyarsky.hw9;

import java.sql.ResultSet;

public interface ExecutorHandler<T> {

    T handle(ResultSet resultSet) throws DbException;
}
