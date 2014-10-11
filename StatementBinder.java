package com.wagerwilly.jorm;

import com.wagerwilly.App;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class StatementBinder {
    public static void bind(PreparedStatement statement, PersistentContext pc, Object o) throws IllegalAccessException, SQLException {
        bind(statement, pc.fields, o);
    }

    private static void bind(PreparedStatement statement, Field[] fields, Object o) throws IllegalAccessException, SQLException {
        for (int i = 0, p = 1; i < fields.length; i++, p++) {
            bind(statement, fields[i].getType(), fields[i].get(o), p);
        }
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
