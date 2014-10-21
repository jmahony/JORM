package com.wagerwilly.jorm;

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
        for (int i = 0; i < pc.persistentUnits.size(); i++) {
            if (pc.persistentUnits.get(i).field.getType() == LocalDate.class) {
                pc.persistentUnits.get(i).field.set(o, resultSet.getDate(pc.persistentUnits.get(i).column).toLocalDate());
            } else if (pc.persistentUnits.get(i).field.getType() == LocalDateTime.class) {
                pc.persistentUnits.get(i).field.set(o, resultSet.getTimestamp(pc.persistentUnits.get(i).column).toLocalDateTime());
            } else {
                pc.persistentUnits.get(i).field.set(o, resultSet.getObject(pc.persistentUnits.get(i).column));
            }
        }
        pc.id.set(o, resultSet.getLong("id"));
        return o;
    }
}
