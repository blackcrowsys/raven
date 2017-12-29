package com.blackcrowsys.raven.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MapTo {
    String fieldName();

    Class using() default Void.class;

    String fromSchemaMethod() default "";

    String toSchemaMethod() default "";
}
