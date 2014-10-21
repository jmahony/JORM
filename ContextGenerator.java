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

    public static BasePersistentContext generateBase(final Class<?> cl, Field field) {
        BasePersistentContext epc = new BasePersistentContext() {{
            c = cl;
            containingField = field;
            persistentUnits = getAllPersistentUnits(this);
        }};
        makeAccessible(epc);
        return epc;
    }

    public static PersistentContext generate(final Class<?> cl) {
        PersistentContext pc = new PersistentContext() {{
            c = cl;
            tableName = getTableName(c);
            id = getId(c);
            persistentUnits = getAllPersistentUnits(this);
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

    private static Field getId(Class<?> c) {
        Field[] id = getAllFieldsWithAnnotation(c, Id.class);
        if (id.length > 0) return id[0];
        throw new JormException("At least one field must be annotated with @Id");
    }

    public static List<PersistentUnit> getAllPersistentUnits(BasePersistentContext pc) {
        List<PersistentUnit> units = new ArrayList<>();
        Field[] fields = getAllFieldsWithAnnotation(pc.c, Persistent.class);
        Arrays.stream(fields).forEach(f ->
            units.add(new PersistentUnit() {{
                field = f;
                c = pc.c;
                context = pc;
                a = Persistent.class;
                column = getColumnName(f);
            }})
        );

        getExpandablePersistent(pc.c).values().stream().forEach(epc ->
                epc.persistentUnits.forEach(units::add));

        return units;
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

    private static Map<Class, BasePersistentContext> getExpandablePersistent(Class<?> c) {
        Field[] fields = getAllFieldsWithAnnotation(c, ExpandablePersistent.class);
        return Arrays.stream(fields).filter(field -> true).collect(Collectors.toMap(Field::getType, f ->
                generateBase(f.getType(), f)));
    }

    private static String getColumnName(Field field) {
        String column = field.getAnnotation(Persistent.class).column();
        return column.isEmpty() ? field.getName() : column;
    }

    private static void makeAccessible(PersistentContext pc) {
        pc.persistentUnits.forEach(pu -> pu.field.setAccessible(true));
        pc.id.setAccessible(true);
    }

    private static void makeAccessible(BasePersistentContext pc) {
        pc.persistentUnits.forEach(pu -> pu.field.setAccessible(true));
    }
}
