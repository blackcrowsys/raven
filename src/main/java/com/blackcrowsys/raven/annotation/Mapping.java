package com.blackcrowsys.raven.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation around a class that couples the value to this class.
 * value: the model class this DTO class is coupled with.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapping {

    /**
     * The class to which this DTO is mapped to.
     *
     * @return the class of the Model to which this DTO is mapped to
     */
    Class value();
}
