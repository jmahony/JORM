package com.wagerwilly.jorm;

import java.lang.reflect.Field;
import java.util.Map;

public class PersistentContext {
    public Class c;
    public String tableName;
    public String[] columns;
    public Field[] fields;
    public Map<Class, ExpandablePersistentContext> expandablePersistents;
    public String insertQuery;
    public String selectQuery;
    public String updateQuery;
    public Field id;
}
