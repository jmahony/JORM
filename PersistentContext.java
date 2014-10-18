package com.wagerwilly.jorm;

import java.lang.reflect.Field;

public class PersistentContext extends ExpandablePersistentContext {
    public String tableName;
    public String insertQuery;
    public String selectQuery;
    public String updateQuery;
    public Field id;
}
