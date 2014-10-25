package com.wagerwilly.jorm;

import com.wagerwilly.jorm.annotations.ExpandablePersistent;
import com.wagerwilly.jorm.annotations.Id;
import com.wagerwilly.jorm.annotations.Persistent;
import com.wagerwilly.jorm.annotations.Table;
import com.wagerwilly.jorm.exceptions.JormException;
import org.junit.Test;

import java.lang.reflect.Field;

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
        assertTrue(pc.persistentUnits.stream().anyMatch(pu -> pu.field.getName().equals("firstName")));
        assertTrue(pc.persistentUnits.stream().anyMatch(pu -> pu.field.getName().equals("firstName")));
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
        assertTrue(pc.persistentUnits.stream().anyMatch(pu -> pu.field.getName().equals("firstName")));
        assertTrue(pc.persistentUnits.stream().anyMatch(pu -> pu.field.getName().equals("lastName")));
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
        assertTrue(pc.persistentUnits.stream().anyMatch(pu -> pu.field.getName().equals("firstName")));
        assertTrue(pc.persistentUnits.stream().anyMatch(pu -> pu.field.getName().equals("lastName")));
        assertTrue(pc.persistentUnits.stream().anyMatch(pu -> pu.field.getName().equals("age")));
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
    public void testGenerateContextWithExpandablePersistentFields() throws NoSuchFieldException {
        PersistentContext pc = ContextGenerator.generate(User6.class);
        Field addressField = User6.class.getDeclaredField("address");

        assertEquals("id", pc.id.getName());
        assertEquals("user", pc.tableName);
        assertTrue(pc.persistentUnits.stream().anyMatch(pu -> pu.field.getName().equals("firstName")));
        assertTrue(pc.persistentUnits.stream().anyMatch(pu -> pu.field.getName().equals("lastName")));
        assertTrue(pc.persistentUnits.stream().anyMatch(pu -> pu.field.getName().equals("streetLineOne")));
        assertTrue(pc.persistentUnits.stream().anyMatch(pu -> pu.field.getName().equals("postcode")));

        pc.persistentUnits.forEach(pu -> {
            if (pu.field.getName().equals("streetLineOne") || pu.field.getName().equals("postcode")) {
                assertEquals(addressField, pu.context.containingField);
            }
        });
    }
}
