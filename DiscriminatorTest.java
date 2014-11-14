package com.wagerwilly.jorm;

import com.wagerwilly.jorm.annotations.Persistent;
import com.wagerwilly.jorm.annotations.Table;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DiscriminatorTest {

    @Table(name = "users")
    @Discriminator(name = UserDiscriminator.class)
    private class User {
        @Persistent
        String name;
    }

    private class Administrator extends User {}

    @interface Discriminator {
        Class name();
    }

    interface DiscriminatorInterface {
        public Class execute(Object o) throws Exception;
    }

    private class UserDiscriminator implements DiscriminatorInterface {
        @Override
        public Class execute(Object o) throws Exception {
            ResultSet rs = (ResultSet) o;
            String discriminatorValue = rs.getString("role");
            switch (discriminatorValue) {
                case "administrator":
                    return Administrator.class;
                case "user":
                    return User.class;
            }
            throw new Exception("Invalid discriminator value");
        }
    }

    @Test
    public void testSimpleDiscriminator() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString("role")).thenReturn("administrator");
        Class o = Administrator.class;
        assertEquals(Administrator.class.getCanonicalName(), o.getCanonicalName());
    }
}
