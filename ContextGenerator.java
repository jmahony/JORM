package com.wagerwilly.jorm;

import com.wagerwilly.jorm.annotations.Id;
import com.wagerwilly.jorm.annotations.Persistent;
import com.wagerwilly.jorm.annotations.Table;
import com.wagerwilly.jorm.exceptions.JormException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class ContextGenerator {
    public static PersistentContext generate(final Class<?> cl) {
        PersistentContext pc = new PersistentContext() {{
            c = cl;
            tableName = getTableName(c);
            fields = getPersistentFields(c);
            columns = getColumns(fields);
            id = getId(c);
            selectQuery = generateSelectQueryString(this);
            insertQuery = generateInsertQueryString(this);
        }};
        makeAccessible(pc);
        return pc;
    }

    private static String getTableName(Class<?> c) {
        String annotationName = c.getAnnotation(Table.class).name();
        return annotationName.isEmpty() ? c.getSimpleName() : annotationName;
    }

    private static Field[] getPersistentFields(Class<?> c) {
        Field[] fields = getAllFieldsWithAnnotation(c, Persistent.class);
        return fields;
    }

    private static Field[] getAllFieldsWithAnnotation(Class<?> c, Class annotation) {
        HashMap<String, Field> fields = new HashMap<>();
        return getAllFieldsWithAnnotation(c, annotation, fields).values().stream().sorted((f1, f2) ->
                f1.getName().compareTo(f2.getName())).toArray(Field[]::new);
    }

    private static HashMap<String, Field> getAllFieldsWithAnnotation(Class<?> c, Class annotation, HashMap<String, Field> fields) {
        fields.putAll(Arrays.stream(c.getDeclaredFields()).filter(field ->
                        field.isAnnotationPresent(annotation) && !fields.containsKey(field.getName())
        ).collect(Collectors.toMap(Field::getName, field -> field)));
        if (c.getSuperclass() != null) {
            getAllFieldsWithAnnotation(c.getSuperclass(), annotation, fields);
        }
        return fields;
    }

    private static String[] getColumns(Field[] fields) {
        return Arrays.stream(fields).map(field -> {
            String column = field.getAnnotation(Persistent.class).column();
            return column.isEmpty() ? field.getName() : column;
        }).toArray(String[]::new);
    }

    private static Field getId(Class<?> c) {
        Field[] id = getAllFieldsWithAnnotation(c, Id.class);
        if (id.length > 0) return id[0];
        throw new JormException("At least one field must be annotated with @Id");
    }

    private static String generateSelectQueryString(PersistentContext pc) {
        return String.format("SELECT * FROM %s WHERE id = {id}", pc.tableName);
    }

    private static String generateInsertQueryString(PersistentContext pc) {
        String sql = String.format("INSERT INTO %s (%s) VALUES (%s) RETURNING *", pc.tableName, "%s", "%s");
        return addParametersToQueryString(sql, pc);
    }

    private static String addParametersToQueryString(String sql, PersistentContext pc) {
        for (int i = 0; i < pc.fields.length; i++) {
            sql = addParameterToQueryString(sql, pc, i);
        }
        return sql;
    }

    private static String addParameterToQueryString(String sql, PersistentContext pc, int position) {
        String columnsSeparator = position == pc.fields.length - 1 ? "" : ", %s";
        String valuesSeparator = position == pc.fields.length - 1 ? "" : ", %s";
        return String.format(sql, pc.columns[position] + columnsSeparator, "?" + valuesSeparator);
    }

    private static void makeAccessible(PersistentContext pc) {
        Arrays.stream(pc.fields).forEach(field -> field.setAccessible(true));
        pc.id.setAccessible(true);
    }
}
