package com.wagerwilly.jorm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Jorm {
    public static Map<Class, PersistentContext> contexts = new HashMap<>();

    public static PreparedStatement getInsertStatement(Connection connection, Object o) throws IllegalAccessException, SQLException {
        PersistentContext context = createContext(o.getClass());
        PreparedStatement statement = connection.prepareStatement(context.insertQuery.replace("{id}", context.id.get(o).toString()));
        StatementBinder.bind(statement, context, o);
        return statement;
    }

    public static PreparedStatement getSelectStatement(Connection connection, Class<?> c, Long id) throws IllegalAccessException, SQLException {
        PersistentContext context = createContext(c);
        return connection.prepareStatement(context.selectQuery.replace("{id}", id.toString()));
    }

    public static PersistentContext createContext(Class<?> c) {
        if (!contexts.containsKey(c)) contexts.put(c, ContextGenerator.generate(c));
        return contexts.get(c);
    }
}
