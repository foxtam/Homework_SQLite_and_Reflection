package net.foxtam.lesson9_homework;

import net.foxtam.lesson9_homework.annotations.Column;
import net.foxtam.lesson9_homework.annotations.Table;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class DBTable<T> {
    private final Class<T> aClass;

    public DBTable(Class<T> aClass) {
        if (!aClass.isAnnotationPresent(Table.class)) {
            throw new IllegalArgumentException(aClass.toString());
        }
        this.aClass = aClass;
    }

    public String getTableCreationSQL() {
        StringBuilder builder =
                new StringBuilder("create table IF NOT EXISTS %s (id integer primary key autoincrement".formatted(getTitle()));
        
        Arrays.stream(aClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Column.class))
                .forEach(field ->
                        builder
                                .append(',')
                                .append(field.getName())
                                .append(" ")
                                .append(transformToSQLiteType(field.getType())));

        return builder.append(");").toString();
    }

    private String transformToSQLiteType(Class<?> type) {
        if (int.class.equals(type)) {
            return "integer";
        } else if (String.class.equals(type)) {
            return "text";
        }
        throw new IllegalArgumentException(type.toString());
    }

    public String getTitle() {
        Table annotation = aClass.getAnnotation(Table.class);
        return annotation.title();
    }

    public List<String> getColumnNames() {
        return Arrays.stream(aClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Column.class))
                .map(Field::getName)
                .toList();
    }
}
