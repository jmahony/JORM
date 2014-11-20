package com.wagerwilly.jorm;

import com.wagerwilly.jorm.annotations.Discriminator;
import com.wagerwilly.jorm.exceptions.DiscriminatorException;
import org.junit.Test;

import java.sql.ResultSet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DiscriminatorTest {
    @Test
    public void testSimpleDiscriminator() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString("role")).thenReturn("administrator");

        Class o = new DiscriminatorEvaluator<User>().discriminate(rs, User.class);

        assertEquals(Administrator.class, o);
    }

    @Test(expected = DiscriminatorException.class)
    public void testClassNoDiscriminatorAnnotation() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString("role")).thenReturn("administrator");

        Class o = new DiscriminatorEvaluator<UserWithNoDiscriminator>().discriminate(rs, UserWithNoDiscriminator.class);
    }
}

@Discriminator(discriminatorClass = UserDiscriminator.class)
class User {}

class Administrator extends User {}

class UserDiscriminator implements DiscriminatorInterface {
    public UserDiscriminator() {}
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

class UserWithNoDiscriminator {}
