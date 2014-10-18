package com.wagerwilly.jorm;

import java.lang.reflect.Field;
import java.util.Map;

public class BasePersistentContext {
    public Class c;
    public String[] columns;
    public Field[] fields;
    public Map<Class, BasePersistentContext> expandablePersistents;
}