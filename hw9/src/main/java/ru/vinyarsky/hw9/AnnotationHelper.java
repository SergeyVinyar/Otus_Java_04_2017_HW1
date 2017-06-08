package ru.vinyarsky.hw9;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * Утилитные методы, работающие с аннотациями JPA
 */
/* package */ final class AnnotationHelper {

    private AnnotationHelper() {
    }

    /**
     * Возвращает наименование таблицы в БД
     */
    public static String getDbTableName(Class<?> clazz) {
        String result = clazz.getName();
        Table tableAnnotation = clazz.getDeclaredAnnotation(Table.class);
        if (tableAnnotation != null && !"".equals(tableAnnotation.name()))
            result = tableAnnotation.name();
        return result;
    }

    /**
     * Возвращает наименование поля-первичного ключа в БД
     */
    public static String getDbPrimaryKeyFieldName(Class<?> clazz) {
        ArrayList<Field> fields = new ArrayList<>();
        Class<?> c = clazz;
        while (c != null) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
            c = c.getSuperclass();
        }
        Optional<Field> idField = fields.stream()
                .filter(field -> field.getDeclaredAnnotation(Id.class) != null)
                .findFirst();
        assert idField.isPresent();
        return getDbFieldName(idField.get());
    }

    /**
     * Возвращает наименование поля в БД
     */
    public static String getDbFieldName(Class<?> clazz, String classFieldName) {
        ArrayList<Field> fields = new ArrayList<>();
        Class<?> c = clazz;
        while (c != null) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
            c = c.getSuperclass();
        }
        Optional<Field> idField = fields.stream()
                .filter(field -> field.getName().equals(classFieldName))
                .findFirst();
        assert idField.isPresent();
        return getDbFieldName(idField.get());
    }

    /**
     * Возвращает наименование поля в БД
     */
    public static String getDbFieldName(Field field) {
        String result = field.getName();
        Column columnAnnotation = field.getDeclaredAnnotation(Column.class);
        if (columnAnnotation != null && !"".equals(columnAnnotation.name()))
            result = columnAnnotation.name();
        return result;
    }
}
