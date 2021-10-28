package com.geekbrains.vtb24.lesson9_homework;

import com.geekbrains.vtb24.lesson9_homework.annotations.Column;

import java.lang.ref.Cleaner;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SingleTableSQLiteDB<T> implements AutoCloseable {
    private static final Cleaner CLEANER = Cleaner.create();

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private final DBTable<T> table;
    private final Connection connection;

    public SingleTableSQLiteDB(Path dbPath, DBTable<T> table) throws SQLException {
        this.table = table;
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath.toString());
        CLEANER.register(this, checkCloseConnection(connection));
        createTable(table);
    }

    private static Runnable checkCloseConnection(Connection connection) {
        return () -> {
            try {
                if (!connection.isClosed()) {
                    throw new IllegalStateException();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private void createTable(DBTable<T> table) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String sql = table.getTableCreationSQL();
            statement.execute(sql);
        }
    }

    public void add(T obj) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(getInsertSQL(obj));
        }
    }

    private String getInsertSQL(T obj) {
        return "insert into %s (%s) values (%s);".formatted(
                table.getTitle(),
                String.join(",", table.getColumnNames()),
                getFieldsString(obj));
    }

    private String getFieldsString(T obj) {
        return Arrays.stream(obj.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Column.class))
                .map(getFieldValueFunc(obj))
                .map(v -> "\"" + v + "\"")
                .collect(Collectors.joining(","));
    }

    private Function<Field, String> getFieldValueFunc(T obj) {
        return field -> {
            try {
                field.setAccessible(true);
                return field.get(obj).toString();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
