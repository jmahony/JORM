package com.wagerwilly.jorm;

import java.lang.reflect.Field;

public class PersistentContext {
    public Class c;
    public String tableName;
    public String[] columns;
    public String[] expandableColumns;
    public Field[] fields;
    public Field[] expandableFields;
    public String insertQuery;
    public String selectQuery;
    public String updateQuery;
    public Field id;
}
