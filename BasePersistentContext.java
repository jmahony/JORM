package com.wagerwilly.jorm;

import java.lang.reflect.Field;
import java.util.List;

public class BasePersistentContext {
    public Class c;
    public Field containingField;
    public List<PersistentUnit> persistentUnits;
}
