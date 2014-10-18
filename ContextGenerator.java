package com.wagerwilly.jorm;

import com.wagerwilly.jorm.annotations.ExpandablePersistent;
import com.wagerwilly.jorm.annotations.Id;
import com.wagerwilly.jorm.annotations.Persistent;
import com.wagerwilly.jorm.annotations.Table;
import com.wagerwilly.jorm.exceptions.JormException;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class ContextGenerator {

    public static BasePersistentContext generateBase(final Class<?> cl) {
        BasePersistentContext epc = new BasePersistentContext() {{
            c = cl;
            fields = getPersistentFields(c);
            columns = getColumns(fields);
            expandablePersistents = getExpandablePersistent(c);
            allFields = getAllFields(this);
            allColumns = getAllColumns(this);
        }};
        makeAccessible(epc);
        return epc;
    }

    public static PersistentContext generate(final Class<?> cl) {
        PersistentContext pc = new PersistentContext() {{
            c = cl;
            tableName = getTableName(c);
            fields = getPersistentFields(c);
            columns = getColumns(fields);
            expandablePersistents = getExpandablePersistent(c);
            id = getId(c);
            allFields = getAllFields(this);
            allColumns = getAllColumns(this);
            selectQuery = QueryGenerator.generateSelectQueryString(this);
            insertQuery = QueryGenerator.generateInsertQueryString(this);
            updateQuery = QueryGenerator.generateUpdateQueryString(this);
        }};
        makeAccessible(pc);
        return pc;
    }

    private static String getTableName(Class<?> c) {
        String annotationName = c.getAnnotation(Table.class).name();
        return annotationName.isEmpty() ? c.getSimpleName() : annotationName;
    }

    private static Field[] getPersistentFields(Class<?> c) {
        return getAllFieldsWithAnnotation(c, Persistent.class);
    }

    private static Map<Class, BasePersistentContext> getExpandablePersistent(Class<?> c) {
        Field[] fields = getAllFieldsWithAnnotation(c, ExpandablePersistent.class);
        return Arrays.stream(fields).filter(field -> true).collect(Collectors.toMap(Field::getType, f ->
                generateBase(f.getType())));
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

    private static void makeAccessible(PersistentContext pc) {
        Arrays.stream(pc.fields).forEach(field -> field.setAccessible(true));
        pc.id.setAccessible(true);
    }

    private static void makeAccessible(BasePersistentContext pc) {
        Arrays.stream(pc.fields).forEach(field -> field.setAccessible(true));
    }

    private static Field[] getAllFields(BasePersistentContext pc) {
        return getAllFields(pc, new ArrayList<>()).stream().toArray(Field[]::new);
    }

    private static List<Field> getAllFields(BasePersistentContext pc, List<Field> fields) {
        fields.addAll(Arrays.asList(pc.fields));
        pc.expandablePersistents.values().forEach(v -> getAllFields(v, fields));
        return fields;
    }

    private static String[] getAllColumns(BasePersistentContext pc) {
        return getAllColumns(pc, new ArrayList<>()).stream().toArray(String[]::new);
    }

    private static List<String> getAllColumns(BasePersistentContext pc, List<String> columns) {
        columns.addAll(Arrays.asList(pc.columns));
        pc.expandablePersistents.values().forEach(v -> getAllColumns(v, columns));
        return columns;
    }
}
