package com.wagerwilly.jorm;

import com.wagerwilly.jorm.annotations.Discriminator;
import com.wagerwilly.jorm.exceptions.DiscriminatorException;

import java.sql.ResultSet;

public class DiscriminatorEvaluator {
    public static Class discriminate(ResultSet rs, Class<?> c) throws Exception {
        Class<? extends DiscriminatorInterface> discriminatorClass = getDiscriminatorClass(c);
        DiscriminatorInterface discriminator = discriminatorClass.newInstance();
        return discriminator.execute(rs);
    }
    private static Class<? extends DiscriminatorInterface> getDiscriminatorClass(Class<?> c) {
        try {
            return c.getAnnotation(Discriminator.class).discriminatorClass();
        } catch (NullPointerException e) {
            throw new DiscriminatorException("No Discriminator annotation could on class " + c.getCanonicalName());
        }
    }
}
