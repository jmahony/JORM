package com.wagerwilly.jorm;

import com.wagerwilly.App;
import com.wagerwilly.Database;
import com.wagerwilly.DatabaseImpl;
import com.wagerwilly.jorm.annotations.Persistent;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

class TestClass {
    public enum Colour { RED, BLUE };
    String username;
    int age;
    long balance;
    Colour colour;
    LocalDateTime joinedOn;
    LocalDate dateOfBirth;
    Address address;
}

class Address {
    String addressLineOne;
}

public class StatementBinderTest {

    @Test
    public void testSimpleInsertBind() throws SQLException, IllegalAccessException {
        Database db = new DatabaseImpl(App.TEST_DATABASE_PROPERTIES_FILE_NAME);
        String sql = "INSERT INTO users (username, age, balance, colour, joinedOn, dateOfBirth) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = db.getConnection().prepareStatement(sql);
        PersistentContext pc = new PersistentContext();

        TestClass tc = new TestClass() {{
            username = "josh";
            age = 27;
            balance = 100;
            colour = Colour.RED;
            joinedOn = LocalDateTime.parse("1999-01-08 04:05:06", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            dateOfBirth = LocalDate.parse("1987-08-04", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }};

        Field[] fields = Arrays.stream(TestClass.class.getDeclaredFields()).filter(field ->
                field.getName().equals("username") || field.getName().equals("age") ||
                field.getName().equals("balance") || field.getName().equals("colour") ||
                field.getName().equals("joinedOn") || field.getName().equals("dateOfBirth")
        ).toArray(Field[]::new);

        pc.persistentUnits = new ArrayList<>();

        Arrays.stream(fields).forEach(f ->
            pc.persistentUnits.add(new PersistentUnit() {{
                field = f;
                c = pc.c;
                context = pc;
                a = Persistent.class;
                column = f.getName();
            }})
        );

        StatementBinder.bind(statement, pc, tc);
        assertEquals("INSERT INTO users (username, age, balance, colour, joinedOn, dateOfBirth) VALUES ('josh', '27', '100', 'RED', '1999-01-08 04:05:06.000000 +00:00:00', '1987-08-04 +01:00:00')", statement.toString());
    }

    @Test
    public void testSimpleUpdateBind() throws SQLException, IllegalAccessException {
        Database db = new DatabaseImpl(App.TEST_DATABASE_PROPERTIES_FILE_NAME);
        String sql = "UPDATE users SET username = ?, age = ?, balance = ?, colour = ?, joinedOn = ?, dateOfBirth = ? WHERE id = {id} RETURNING *";
        PreparedStatement statement = db.getConnection().prepareStatement(sql);
        PersistentContext pc = new PersistentContext();

        TestClass tc = new TestClass() {{
            username = "josh";
            age = 27;
            balance = 100;
            colour = Colour.RED;
            joinedOn = LocalDateTime.parse("1999-01-08 04:05:06", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            dateOfBirth = LocalDate.parse("1987-08-04", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }};

        Field[] fields = Arrays.stream(TestClass.class.getDeclaredFields()).filter(field ->
                        field.getName().equals("username") || field.getName().equals("age") ||
                        field.getName().equals("balance") || field.getName().equals("colour") ||
                        field.getName().equals("joinedOn") || field.getName().equals("dateOfBirth")
        ).toArray(Field[]::new);

        pc.persistentUnits = new ArrayList<>();

        Arrays.stream(fields).forEach(f ->
            pc.persistentUnits.add(new PersistentUnit() {{
                field = f;
                c = pc.c;
                context = pc;
                a = Persistent.class;
                column = f.getName();
            }})
        );

        StatementBinder.bind(statement, pc, tc);

        assertEquals("UPDATE users SET username = 'josh', age = '27', balance = '100', colour = 'RED', joinedOn = '1999-01-08 04:05:06.000000 +00:00:00', dateOfBirth = '1987-08-04 +01:00:00' WHERE id = {id} RETURNING *", statement.toString());
    }

    @Test
    public void testExpandableInsertBind() throws SQLException, NoSuchFieldException, IllegalAccessException {
        Database db = new DatabaseImpl(App.TEST_DATABASE_PROPERTIES_FILE_NAME);
        String sql = "INSERT INTO users (username, addressLineOne) VALUES (?, ?)";
        PreparedStatement statement = db.getConnection().prepareStatement(sql);
        PersistentContext pc = new PersistentContext();

        TestClass tc = new TestClass() {{
            username = "josh";
            address = new Address() {{ addressLineOne = "56 Forge Rise"; }};
        }};

        Field[] fields = Arrays.stream(TestClass.class.getDeclaredFields()).filter(field ->
            field.getName().equals("username")
        ).toArray(Field[]::new);

        pc.persistentUnits = new ArrayList<>();

        Arrays.stream(fields).forEach(f ->
            pc.persistentUnits.add(new PersistentUnit() {{
                field = f;
                c = pc.c;
                context = pc;
                a = Persistent.class;
                column = f.getName();
            }})
        );

        fields = Arrays.stream(Address.class.getDeclaredFields()).filter(field ->
            field.getName().equals("addressLineOne")
        ).toArray(Field[]::new);

        PersistentContext epc = new PersistentContext() {{
            containingField = TestClass.class.getDeclaredField("address");
        }};

        Arrays.stream(fields).forEach(f ->
            pc.persistentUnits.add(new PersistentUnit() {{
                field = f;
                c = pc.c;
                context = epc;
                a = Persistent.class;
                column = f.getName();
            }})
        );

        StatementBinder.bind(statement, pc, tc);

        assertEquals("INSERT INTO users (username, addressLineOne) VALUES ('josh', '56 Forge Rise')", statement.toString());
    }
}
