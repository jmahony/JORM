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
        PreparedStatement statement = connection.prepareStatement(getInsertQuery(context, o));
        StatementBinder.bind(statement, context, o);
        return statement;
    }

    public static PreparedStatement getSelectStatement(Connection connection, Class<?> c, Long id) throws IllegalAccessException, SQLException {
        PersistentContext context = createContext(c);
        return connection.prepareStatement(getSelectQuery(context, id));
    }

    public static PreparedStatement getUpdateStatement(Connection connection, Class<?> c, Object o) throws SQLException, IllegalAccessException {
        PersistentContext context = createContext(c);
        PreparedStatement statement = connection.prepareStatement(getUpdateQuery(context, o));
        StatementBinder.bind(statement, context, o);
        return statement;
    }

    public static PersistentContext createContext(Class<?> c) {
        if (!contexts.containsKey(c)) contexts.put(c, ContextGenerator.generate(c));
        return contexts.get(c);
    }

    private static String getInsertQuery(PersistentContext context, Object o) throws IllegalAccessException {
        return bindIDToQuery(context.insertQuery, context, o);
    }

    private static String getSelectQuery(PersistentContext context, Long id) throws IllegalAccessException {
        return bindIDToQuery(context.selectQuery, id);
    }

    private static String getUpdateQuery(PersistentContext context, Object o) throws IllegalAccessException {
        return bindIDToQuery(context.updateQuery, context, o);
    }

    private static String bindIDToQuery(String sql, PersistentContext context, Object o) throws IllegalAccessException {
        return bindIDToQuery(sql, (Long) context.id.get(o));
    }

    private static String bindIDToQuery(String sql, Long id) {
        return sql.replace("{id}", id.toString());
    }
}
