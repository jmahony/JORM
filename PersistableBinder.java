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
            if (pc.fields[i].getType() == LocalDate.class) {
                pc.fields[i].set(o, resultSet.getDate(pc.columns[i]).toLocalDate());
            } else if (pc.fields[i].getType() == LocalDateTime.class) {
                pc.fields[i].set(o, resultSet.getTimestamp(pc.columns[i]).toLocalDateTime());
            } else {
                pc.fields[i].set(o, resultSet.getObject(pc.columns[i]));
            }
        }
        pc.id.set(o, resultSet.getLong("id"));
        return o;
    }
}
