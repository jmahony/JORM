package com.wagerwilly.jorm;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class QueryGeneratorTest {
    @Test
    public void testGeneratorSelectQuery() throws IllegalAccessException {
        PersistentContext pc = new PersistentContext() {{
            tableName = "users";
        }};
        assertEquals("SELECT * FROM users WHERE id = {id}", QueryGenerator.generateSelectQueryString(pc));
    }

    @Test
    public void testGeneratorInsertQuery() throws IllegalAccessException {
        List<PersistentUnit> units = new ArrayList<>();
        PersistentUnit firstNameUnit = new PersistentUnit() {{ column = "firstName"; }};
        PersistentUnit lastNameUnit = new PersistentUnit() {{ column = "lastName"; }};
        units.add(firstNameUnit);
        units.add(lastNameUnit);
        PersistentContext pc = new PersistentContext() {{
            tableName = "users";
            persistentUnits = units;
        }};
        assertEquals("INSERT INTO users (firstName, lastName) VALUES (?, ?) RETURNING *", QueryGenerator.generateInsertQueryString(pc));
    }

    @Test
    public void testGeneratorUpdateQuery() {
        List<PersistentUnit> units = new ArrayList<>();
        PersistentUnit firstNameUnit = new PersistentUnit() {{ column = "firstName"; }};
        PersistentUnit lastNameUnit = new PersistentUnit() {{ column = "lastName"; }};
        units.add(firstNameUnit);
        units.add(lastNameUnit);
        PersistentContext pc = new PersistentContext() {{
            tableName = "users";
            persistentUnits = units;
        }};
        assertEquals("UPDATE users SET firstName=?, lastName=? WHERE id = {id} RETURNING *", QueryGenerator.generateUpdateQueryString(pc));
    }

    @Test
    public void testGeneratorDeleteQuery() {
        PersistentContext pc = new PersistentContext() {{
            tableName = "users";
        }};
        assertEquals("DELETE FROM users WHERE id = {id}", QueryGenerator.generateDeleteQueryString(pc));
    }

    @Test
    public void testGeneratorInsertQueryWithTypeCast() throws IllegalAccessException {
        List<PersistentUnit> units = new ArrayList<>();
        PersistentUnit firstNameUnit = new PersistentUnit() {{ column = "firstName"; }};
        PersistentUnit lastNameUnit = new PersistentUnit() {{ column = "lastName"; }};
        PersistentUnit userLevel = new PersistentUnit() {{ column = "level"; castTo = "user_level"; }};
        units.add(firstNameUnit);
        units.add(lastNameUnit);
        units.add(userLevel);
        PersistentContext pc = new PersistentContext() {{
            tableName = "users";
            persistentUnits = units;
        }};
        assertEquals("INSERT INTO users (firstName, lastName, level) VALUES (?, ?, CAST(? AS user_level)) RETURNING *", QueryGenerator.generateInsertQueryString(pc));
    }
}
