package com.wagerwilly.jorm;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class BasePersistentContext {
    public Class c;
    public Map<Class, BasePersistentContext> expandablePersistents;
    public Field containingField;
    public List<PersistentUnit> persistentUnits;
}
