package com.wagerwilly.jorm;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PersistableBinder {
    public static <T> T bind(PersistentContext pc, ResultSet resultSet, Class<T> c) throws SQLException, IllegalAccessException, InstantiationException {
        T o = c.newInstance();
        resultSet.next();

        for (int i = 0; i < pc.persistentUnits.size(); i++) {
            bind(pc.persistentUnits.get(i), resultSet, o);
        }

        pc.id.set(o, resultSet.getLong("id"));
        return o;
    }

    private static void bind(PersistentUnit pu, ResultSet resultSet, Object o) throws InstantiationException, IllegalAccessException, SQLException {
        Object p = getOperable(pu, o);
        if (pu.field.getType() == LocalDate.class) {
            pu.field.set(p, resultSet.getDate(pu.column).toLocalDate());
        } else if (pu.field.getType() == LocalDateTime.class) {
            pu.field.set(p, resultSet.getTimestamp(pu.column).toLocalDateTime());
        } else {
            pu.field.set(p, resultSet.getObject(pu.column));
        }
    }

    private static Object getOperable(PersistentUnit pu, Object o) throws IllegalAccessException, InstantiationException {
        if (pu.context.containingField == null) return o;

        Field containing = pu.context.containingField;

        if (objectsFieldHasValue(o, containing)) return containing.get(o);

        Object q = getContextClassInstance(pu.context);
        containing.set(o, q);

        return q;
    }

    private static boolean objectsFieldHasValue(Object o, Field field) throws IllegalAccessException {
        return field.get(o) != null;
    }

    private static Object getContextClassInstance(BasePersistentContext context) throws IllegalAccessException, InstantiationException {
        return context.c.newInstance();
    }
}
