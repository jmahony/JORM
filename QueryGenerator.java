package com.wagerwilly.jorm;

public class QueryGenerator {
    static String generateSelectQueryString(PersistentContext pc) {
        return String.format("SELECT * FROM %s WHERE id = {id}", pc.tableName);
    }

    static String generateInsertQueryString(PersistentContext pc) {
        String sql = String.format("INSERT INTO %s (%s) VALUES (%s) RETURNING *", pc.tableName, "%s", "%s");
        return addParametersToQueryString(sql, pc);
    }

    private static String addParametersToQueryString(String sql, BasePersistentContext pc) {
        for (int i = 0; i < pc.allFields.length; i++) {
            sql = addParameterToQueryString(sql, pc, i);
        }
        return sql;
    }

    private static String addParameterToQueryString(String sql, BasePersistentContext pc, int position) {
        String separator = position == pc.allFields.length - 1 ? "" : ", %s";
        return String.format(sql, pc.allColumns[position] + separator, "?" + separator);
    }

    static String generateUpdateQueryString(PersistentContext pc) {
        String sql = String.format("UPDATE %s SET %s WHERE id = {id} RETURNING *", pc.tableName, "%s");
        return addParametersToUpdateQueryString(sql, pc);
    }

    private static String addParametersToUpdateQueryString(String sql, BasePersistentContext pc) {
        for (int i = 0; i < pc.allFields.length; i++) {
            sql = addParameterToUpdateQueryString(sql, pc, i);
        }
        return sql;
    }

    private static String addParameterToUpdateQueryString(String sql, BasePersistentContext pc, int position) {
        String separator = position == pc.allFields.length - 1 ? "" : ", %s";
        String columnValuePair = String.format("%s=?%s", pc.allColumns[position], separator);
        return String.format(sql, columnValuePair);
    }
}
