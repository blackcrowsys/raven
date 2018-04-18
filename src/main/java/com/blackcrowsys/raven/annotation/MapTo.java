package com.blackcrowsys.raven.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation used to map a field/property/attribute of a DTO to Model class.
 * fieldName: the name of the field that it maps to - required
 * using: a static class that is used to convert - optional
 * fromSchemaMethod: static method of the static class to use when converting from DTO to Model - optional
 * toSchemaMethod: static method of the static class to use when converting from Model to DTO - optional
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MapTo {

    /**
     * Maps the field to the field of the mapped class.
     *
     * @return the name of the field that this field maps to
     */
    String fieldName();

    /**
     * Sets the class with static methods that are used to convert from DTO to Model.
     *
     * @return void class or, if set, the class with static methods.
     */
    Class using() default Void.class;

    /**
     * The name of the static method that is used to convert from DTO to Model field/s.
     *
     * @return the name of the static method
     */
    String fromSchemaMethod() default "";

    /**
     * The name of the static method that is used to convert from Model to DTO field/s.
     *
     * @return the name of the static method
     */
    String toSchemaMethod() default "";
}
