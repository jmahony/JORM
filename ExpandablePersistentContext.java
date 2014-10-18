package com.wagerwilly.jorm;

import java.lang.reflect.Field;
import java.util.Map;

public class ExpandablePersistentContext {
    public Class c;
    public String[] columns;
    public Field[] fields;
    public Map<Class, ExpandablePersistentContext> expandablePersistents;
}
