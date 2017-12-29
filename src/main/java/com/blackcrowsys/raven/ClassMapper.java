package com.blackcrowsys.raven;

import com.blackcrowsys.raven.annotation.MapTo;
import com.blackcrowsys.raven.annotation.Mapping;
import com.blackcrowsys.raven.exceptions.InvalidMappingException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ClassMapper {

    public static Object mapFromSchema(Object srcObject) throws IllegalAccessException, InstantiationException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        Class targetClass = getTargetClass(srcObject);
        Object target = targetClass.newInstance();
        Field[] srcFields = srcObject.getClass().getDeclaredFields();
        for (Field srcField : srcFields) {
            srcField.setAccessible(true);
            MapTo mapTo = srcField.getAnnotation(MapTo.class);
            if (mapTo != null) {
                String destinationFieldName = mapTo.fieldName();
                if (mapTo.using() == Void.class) {
                    Field dstField = target.getClass().getDeclaredField(destinationFieldName);
                    if (dstField == null) {
                        throw new InvalidMappingException("No destination field " + destinationFieldName);
                    }
                    dstField.setAccessible(true);
                    if (srcField.getClass() != dstField.getClass()) {
                        throw new InvalidMappingException("Source and desitination mapping does not match for " + srcField.getName());
                    }
                    Object newValue = srcField.get(srcObject);
                    dstField.set(target, newValue);
                }
                if (mapTo.using() != Void.class && mapTo.fromSchemaMethod().equals("")) {
                    throw new InvalidMappingException("Incomplete mapping for " + srcField.getName());
                }
                if (mapTo.using() != Void.class && !mapTo.fromSchemaMethod().equals("")) {
                    Method method = mapTo.using().getMethod(mapTo.fromSchemaMethod(), srcField.getType());
                    if (method == null) {
                        throw new InvalidMappingException("Invalid fromSchemaMethod on mapping for " + srcField.getName());
                    }
                    Object newValue = method.invoke(mapTo.using().newInstance(), srcField.get(srcObject));
                    Field dstField = target.getClass().getDeclaredField(destinationFieldName);
                    if (dstField == null) {
                        throw new InvalidMappingException("No destination field " + destinationFieldName);
                    }
                    dstField.setAccessible(true);
                    dstField.set(target, newValue);
                }
            }
        }
        return target;
    }

    private static Class getTargetClass(Object o) {
        Mapping mapping = o.getClass().getAnnotation(Mapping.class);
        return mapping.value();
    }

    public static Object mapToSchema(Class clazz, Object o) throws IllegalAccessException, InstantiationException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        Mapping mapping = (Mapping) clazz.getAnnotation(Mapping.class);
        if (mapping == null) {
            throw new InvalidMappingException("Class is not a schema: " + clazz.getName());
        }
        if (!o.getClass().getName().equals(mapping.value().getName())) {
            throw new InvalidMappingException("Class mapping does not match " + clazz.getName() + " with " + o.getClass().getName());
        }
        Object destination = clazz.newInstance();
        Field[] dstFields = destination.getClass().getDeclaredFields();
        for (Field dstField : dstFields) {
            dstField.setAccessible(true);
            MapTo mapTo = dstField.getAnnotation(MapTo.class);
            if (mapTo != null) {
                String fieldName = mapTo.fieldName();
                Field srcField = o.getClass().getDeclaredField(fieldName);
                if (srcField == null) {
                    throw new InvalidMappingException("Invalid mapping on field " + fieldName);
                }
                srcField.setAccessible(true);
                if (mapTo.using() == Void.class) {

                    if (dstField.getClass() != srcField.getClass()) {
                        throw new InvalidMappingException("Source and destination mapping does not match for " + fieldName);
                    }
                    dstField.set(destination, srcField.get(o));
                }
                if (mapTo.using() != Void.class && mapTo.toSchemaMethod().equals("")) {
                    throw new InvalidMappingException("Invalid toSchemaMethod on mapping for " + fieldName);
                }
                if (mapTo.using() != Void.class && !mapTo.toSchemaMethod().equals("")) {
                    Method method = mapTo.using().getMethod(mapTo.toSchemaMethod(), srcField.getType());
                    if (method == null) {
                        throw new InvalidMappingException("Invalid toSchemaMethod on mapping for " + fieldName);
                    }
                    Object newValue = method.invoke(mapTo.using().newInstance(), srcField.get(o));
                    dstField.set(destination, newValue);
                }
            }
        }


        return destination;
    }
}
