package com.wagerwilly.jorm.annotations;

import com.wagerwilly.jorm.DiscriminatorInterface;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Discriminator {
    Class<? extends DiscriminatorInterface> discriminatorClass();
}
