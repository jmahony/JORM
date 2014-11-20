package com.wagerwilly.jorm;

import com.wagerwilly.jorm.annotations.Discriminator;
import com.wagerwilly.jorm.exceptions.DiscriminatorException;

import java.sql.ResultSet;

public class DiscriminatorEvaluator<T> {
    public Class<? extends T> discriminate(ResultSet rs, Class<T> c) throws DiscriminatorException {
        try {
            Class<? extends DiscriminatorInterface> discriminatorClass = getDiscriminatorClass(c);
            DiscriminatorInterface discriminator = discriminatorClass.newInstance();
            return discriminator.execute(rs);
        } catch (Exception e) {
            throw new DiscriminatorException(e.getMessage());
        }
    }
    private Class<? extends DiscriminatorInterface> getDiscriminatorClass(Class<?> c) {
        try {
            return c.getAnnotation(Discriminator.class).discriminatorClass();
        } catch (NullPointerException e) {
            throw new DiscriminatorException("No Discriminator annotation could on class " + c.getCanonicalName());
        }
    }
}
