package com.wagerwilly.jorm;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class PersistableBinder {
    public static <T> T bind(PersistentContext pc, ResultSet resultSet, Class<T> c) throws SQLException, IllegalAccessException, InstantiationException {
        T o = c.newInstance();
        Map<String, Object> results = new HashMap<>();
        resultSet.next();
        PersistentUnit currentUnit;
        Field currentField;
        String currentColumn;
        Object p;
        for (int i = 0; i < pc.persistentUnits.size(); i++) {
            currentUnit = pc.persistentUnits.get(i);
            p = getOperable(currentUnit, o);
            currentField = currentUnit.field;
            currentColumn = currentUnit.column;
            if (currentField.getType() == LocalDate.class) {
                currentField.set(p, resultSet.getDate(currentColumn).toLocalDate());
            } else if (pc.persistentUnits.get(i).field.getType() == LocalDateTime.class) {
                currentField.set(p, resultSet.getTimestamp(currentColumn).toLocalDateTime());
            } else {
                currentField.set(p, resultSet.getObject(currentColumn));
            }
        }
        pc.id.set(o, resultSet.getLong("id"));
        return o;
    }


    private static Object getOperable(PersistentUnit persistentUnit, Object o) throws IllegalAccessException, InstantiationException {
        Object q;
        if (persistentUnit.context.containingField != null) {
            Field containing = persistentUnit.context.containingField;
            if (containing.get(o) == null) {
                q = persistentUnit.context.c.newInstance();
                containing.set(o, q);
            } else {
                q = containing.get(o);
            }
        } else {
            q = o;
        }
        return q;
    }
}
