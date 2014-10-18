package com.wagerwilly.jorm;

import org.junit.Test;

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
        PersistentContext pc = new PersistentContext() {{
            tableName = "users";
            allColumns = new String[] {"firstName", "lastName"};
        }};
        assertEquals("INSERT INTO users (firstName, lastName) VALUES (?, ?) RETURNING *", QueryGenerator.generateInsertQueryString(pc));
    }

    @Test
    public void testGeneratorUpdateQuery() {
        PersistentContext pc = new PersistentContext() {{
            tableName = "users";
            allColumns = new String[] {"firstName", "lastName"};
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
}
