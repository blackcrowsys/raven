package com.blackcrowsys.raven;

import com.blackcrowsys.raven.annotation.MapTo;
import com.blackcrowsys.raven.annotation.Mapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class ClassMapper {

    public static Object mapFromSchema(Object o) throws IllegalAccessException, InstantiationException, NoSuchFieldException {
        Class target = getTargetClass(o);

        Field[] fields = new Field[getSchemaFields(target).length];
        fields = getSchemaFields(target);
        for(Field field : fields) {
            getTargetField(field, target);
        }
        return target.newInstance();
    }

    private static Class getTargetClass(Object o) {
        Mapping mapping = o.getClass().getAnnotation(Mapping.class);
        return mapping.value();
    }

    private static Field[] getSchemaFields(Class clazz) {
        return clazz.getDeclaredFields();
    }

    private static Field getTargetField(Field schemaField, Class targetClass) throws NoSuchFieldException, IllegalAccessException {
        Field targetField;
        Annotation[] fieldAnnotations = schemaField.getDeclaredAnnotations();
        for(Annotation fieldAnnotation : fieldAnnotations) {
            if (fieldAnnotation instanceof MapTo) {
                MapTo mapTo = (MapTo) fieldAnnotation;
                targetField = targetClass.getField(mapTo.fieldName());

                return targetField;
            }
        }
        return null;
    }

}
