package com.wagerwilly.jorm.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Persistent {
    String column() default "";
    String castTo() default "";
}
