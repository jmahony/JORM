package com.wagerwilly.jorm;

import com.wagerwilly.App;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class StatementBinder {
    public static void bind(PreparedStatement statement, PersistentContext pc, Object o) throws IllegalAccessException, SQLException {
        bind(statement, pc.persistentUnits, o);
    }

    private static void bind(PreparedStatement statement, List<PersistentUnit> units, Object o) throws IllegalAccessException, SQLException {
        for (int i = 0, position = 1; i < units.size(); i++, position++) {
            bind(statement, units.get(i), o, position);
        }
    }

    private static void bind(PreparedStatement statement, PersistentUnit pu, Object o, int position) throws IllegalAccessException, SQLException {
        Object r = pu.context.containingField == null ? o : pu.context.containingField.get(o);
        bind(statement, pu.field.getType(), pu.field.get(r), position);
    }

    private static void bind(PreparedStatement statement, Class type, Object value, int position) throws SQLException {
        if (type == long.class || type == Long.class) {
            statement.setLong(position, (long) value);
        } else if (type == int.class || type == Integer.class) {
            statement.setInt(position, (int) value);
        } else if (type == String.class || value.getClass().isEnum()) {
            statement.setString(position, value.toString());
        } else if (type == LocalDateTime.class) {
            statement.setTimestamp(position, App.toTimestamp((LocalDateTime) value));
        } else if (type == LocalDate.class) {
            statement.setDate(position, Date.valueOf((LocalDate) value));
        } else if (type == boolean.class || type == Boolean.class) {
            statement.setBoolean(position, (boolean) value);
        }
    }
}
