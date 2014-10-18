package com.wagerwilly.jorm;

import com.wagerwilly.jorm.annotations.ExpandablePersistent;
import com.wagerwilly.jorm.annotations.Id;
import com.wagerwilly.jorm.annotations.Persistent;
import com.wagerwilly.jorm.annotations.Table;
import com.wagerwilly.jorm.exceptions.JormException;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ContextGeneratorTest {
    @Table(name = "user")
    class User1 {}

    @Test(expected = JormException.class)
    public void testTableAnnotationGetName() throws IllegalAccessException {
        PersistentContext pc = ContextGenerator.generate(User1.class);
    }

    @Table()
    class User2 {
        @Id
        long id;
    }

    @Test
    public void testTableAnnotationGetNameWithNoName() throws IllegalAccessException {
        PersistentContext pc = ContextGenerator.generate(User2.class);
        assertEquals("User2", pc.tableName);
    }

    @Table(name = "user")
    class User3 {
        @Id
        long id;
        @Persistent
        String firstName;
    }

    @Test
    public void testGenerateContextSimple() throws IllegalAccessException {
        PersistentContext pc = ContextGenerator.generate(User3.class);
        assertEquals("id", pc.id.getName());
        assertEquals("user", pc.tableName);
        assertTrue(Arrays.stream(pc.fields).anyMatch(field -> field.getName().equals("firstName")));
        assertTrue(Arrays.stream(pc.columns).anyMatch(s -> s.equals("firstName")));
        assertEquals("INSERT INTO user (firstName) VALUES (?) RETURNING *", pc.insertQuery);
        assertEquals("SELECT * FROM user WHERE id = {id}", pc.selectQuery);
    }

    @Table(name = "user")
    class User4 {
        @Id
        long id;
        @Persistent
        String firstName;
        @Persistent
        String lastName;
    }

    @Test
    public void testGenerateContextWithMultipleFields() throws IllegalAccessException {
        PersistentContext pc = ContextGenerator.generate(User4.class);
        assertEquals("id", pc.id.getName());
        assertEquals("user", pc.tableName);
        assertTrue(Arrays.stream(pc.fields).anyMatch(field -> field.getName().equals("firstName")));
        assertTrue(Arrays.stream(pc.columns).anyMatch(s -> s.equals("firstName")));
        assertTrue(Arrays.stream(pc.fields).anyMatch(field -> field.getName().equals("lastName")));
        assertTrue(Arrays.stream(pc.columns).anyMatch(s -> s.equals("lastName")));
        assertEquals("INSERT INTO user (firstName, lastName) VALUES (?, ?) RETURNING *", pc.insertQuery);
        assertEquals("SELECT * FROM user WHERE id = {id}", pc.selectQuery);
    }

    class User5 extends User4 {
        @Persistent
        int age;
    }

    @Test
    public void testGenerateContextWithMultipleFieldsInherited() throws IllegalAccessException {
        PersistentContext pc = ContextGenerator.generate(User5.class);
        assertEquals("id", pc.id.getName());
        assertEquals("user", pc.tableName);
        assertTrue(Arrays.stream(pc.fields).anyMatch(field -> field.getName().equals("firstName")));
        assertTrue(Arrays.stream(pc.columns).anyMatch(s -> s.equals("firstName")));
        assertTrue(Arrays.stream(pc.fields).anyMatch(field -> field.getName().equals("lastName")));
        assertTrue(Arrays.stream(pc.columns).anyMatch(s -> s.equals("lastName")));
        assertTrue(Arrays.stream(pc.fields).anyMatch(field -> field.getName().equals("age")));
        assertTrue(Arrays.stream(pc.columns).anyMatch(s -> s.equals("age")));
        assertEquals("INSERT INTO user (age, firstName, lastName) VALUES (?, ?, ?) RETURNING *", pc.insertQuery);
        assertEquals("SELECT * FROM user WHERE id = {id}", pc.selectQuery);
    }

    class Address {
        @Persistent
        String streetLineOne;
        @Persistent
        String postcode;
    }

    @Table(name = "user")
    class User6 {
        @Id
        long id;
        @Persistent
        String firstName;
        @Persistent
        String lastName;
        @ExpandablePersistent
        Address address;
    }

    @Test
    public void testGenerateContextWithExpandablePersistentFields() {
        PersistentContext pc = ContextGenerator.generate(User6.class);
        assertEquals("id", pc.id.getName());
        assertEquals("user", pc.tableName);
        assertTrue(Arrays.stream(pc.fields).anyMatch(field -> field.getName().equals("firstName")));
        assertTrue(Arrays.stream(pc.columns).anyMatch(s -> s.equals("firstName")));
        assertTrue(Arrays.stream(pc.fields).anyMatch(field -> field.getName().equals("lastName")));
        assertTrue(Arrays.stream(pc.columns).anyMatch(s -> s.equals("lastName")));
        assertTrue(Arrays.stream(pc.fields).anyMatch(field -> field.getName().equals("lastName")));
        assertTrue(Arrays.stream(pc.columns).anyMatch(s -> s.equals("lastName")));
        assertTrue(Arrays.stream(pc.fields).anyMatch(field -> field.getName().equals("streetLineOne")));
        assertTrue(Arrays.stream(pc.columns).anyMatch(s -> s.equals("streetLineOne")));
        assertTrue(Arrays.stream(pc.fields).anyMatch(field -> field.getName().equals("postcode")));
        assertTrue(Arrays.stream(pc.columns).anyMatch(s -> s.equals("postcode")));
    }
}
