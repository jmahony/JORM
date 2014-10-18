package com.wagerwilly.jorm;

import java.lang.reflect.Field;
import java.util.Map;

public class BasePersistentContext {
    public Class c;
    public String[] columns;
    public Field[] fields;
    public Map<Class, BasePersistentContext> expandablePersistents;
    public String[] allColumns;
    public Field[] allFields;
    public Field containingField;
}
