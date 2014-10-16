package com.wagerwilly.jorm;

import java.lang.reflect.Field;

public class PersistentContext {
    public Class c;
    public String tableName;
    public String[] columns;
    public Field[] fields;
    public String insertQuery;
    public String selectQuery;
    public String updateQuery;
    public Field id;
}
