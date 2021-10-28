package net.foxtam.lesson9_homework;

import java.nio.file.Path;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        SingleTableSQLiteDB<Cat> catsDB =
                new SingleTableSQLiteDB<>(
                        Path.of("Animals.db"),
                        new DBTable<>(Cat.class));
        try (catsDB) {
            for (int i = 0; i < 5; i++) {
                catsDB.add(new Cat("Tom " + i, i));
            }
        }
    }
}
