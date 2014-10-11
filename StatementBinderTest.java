package com.wagerwilly.jorm;

import com.wagerwilly.App;
import com.wagerwilly.Database;
import com.wagerwilly.DatabaseImpl;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

class TestClass {
    public enum Colour { RED, BLUE };
    String username;
    int age;
    long balance;
    Colour colour;
    LocalDateTime joinedOn;
    LocalDate dateOfBirth;
}

public class StatementBinderTest {

    @Test
    public void testSimpleBind() throws SQLException, IllegalAccessException {
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

        pc.fields = Arrays.stream(TestClass.class.getDeclaredFields()).filter(field ->
                field.getName().equals("username") || field.getName().equals("age") ||
                field.getName().equals("balance") || field.getName().equals("colour") ||
                field.getName().equals("joinedOn") || field.getName().equals("dateOfBirth")
        ).toArray(Field[]::new);

        StatementBinder.bind(statement, pc, tc);
        assertEquals("INSERT INTO users (username, age, balance, colour, joinedOn, dateOfBirth) VALUES ('josh', '27', '100', 'RED', '1999-01-08 04:05:06.000000 +00:00:00', '1987-08-04 +01:00:00')", statement.toString());
    }
}
