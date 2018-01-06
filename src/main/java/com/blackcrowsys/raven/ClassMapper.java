package com.blackcrowsys.raven;

import com.blackcrowsys.raven.annotation.MapTo;
import com.blackcrowsys.raven.annotation.Mapping;
import com.blackcrowsys.raven.exceptions.InvalidMappingException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * ClassMapper - Using a schema class, usually the DTO class, it can map between a DTO and business model/entity or any
 * other class.
 * The schema class is defined with the @Mapping annotation that defines the class that this is mapped to using:
 *
 * @Mapping(value = MappedClass)
 * <p>
 * Each field is mapped using the @MappedTo annotation on the field.
 */
public class ClassMapper {

    private ClassMapper() {
        // To stop creating instances of this class.
    }

    /**
     * Maps from the DTO to your entity/business model using the schema definition.
     *
     * @param srcObject the schema object, usually the DTO object
     * @return the taget class
     * @throws InvalidMappingException when there is a configuration problem
     */
    public static Object mapFromSchema(Object srcObject) {
        try {
            Class targetClass = getTargetClass(srcObject);
            Object target = targetClass.newInstance();
            setTargetFields(srcObject, target);
            return target;
        } catch (InstantiationException e) {
            throw new InvalidMappingException("Could not create an instance of the target class");
        } catch (IllegalAccessException e) {
            throw new InvalidMappingException("Could not access fields of the schema class");
        }
    }

    public static Object mapToSchema(Class clazz, Object o) {
        Object schema = getSchemaObject(clazz, o);
        Field[] dstFields = schema.getClass().getDeclaredFields();
        setSchemaFields(o, schema, dstFields);
        return schema;
    }

    @SuppressWarnings("squid:S1905")
    private static Object getSchemaObject(Class schemaClazz, Object source) {
        Mapping mapping = (Mapping) schemaClazz.getAnnotation(Mapping.class);
        if (mapping == null) {
            throw new InvalidMappingException("Class is not a schema: " + schemaClazz.getName());
        }
        if (!doesMappingMatch(source.getClass(), mapping)) {
            throw new InvalidMappingException("Class mapping does not match " + schemaClazz.getName() + " with " + source.getClass().getName());
        }
        try {
            return schemaClazz.newInstance();
        } catch (InstantiationException e) {
            throw new InvalidMappingException("Could not create new instance of schema class " + schemaClazz.getName());
        } catch (IllegalAccessException e) {
            throw new InvalidMappingException("Could not access schema fields for " + schemaClazz.getName());
        }
    }

    private static boolean doesMappingMatch(Class clazz, Mapping mapping) {
        return clazz == mapping.value();
    }

    private static void setSchemaFields(Object source, Object schema, Field[] schemaFields) {
        for (Field field : schemaFields) {
            MapTo mapTo = getMapToForField(field);
            if (mapTo != null) {
                setSchemaFieldValue(source, schema, field, mapTo);
            }
        }
    }

    @SuppressWarnings("squid:S2589")
    private static void setSchemaFieldValue(Object source, Object schema, Field field, MapTo mapTo) {
        try {
            String sourceFieldName = mapTo.fieldName();
            Field sourceField = source.getClass().getDeclaredField(sourceFieldName);
            sourceField.setAccessible(true);
            if (mapTo.using() == Void.class && checkFieldTypeMatch(sourceField.getClass(), field.getClass())) {
                setFieldWithValue(schema, field, sourceField.get(source));
            }
            if (usingConverter(mapTo, false)) {
                Method method = mapTo.using().getMethod(mapTo.toSchemaMethod(), sourceField.getType());
                setFieldWithValue(schema, field, method.invoke(mapTo.using().newInstance(), sourceField.get(source)));
            }
        } catch (NoSuchFieldException e) {
            throw new InvalidMappingException("No field in mapped class with name " + mapTo.fieldName());
        } catch (IllegalAccessException e) {
            throw new InvalidMappingException("Could not read source field value for " + mapTo.fieldName());
        } catch (NoSuchMethodException e) {
            throw new InvalidMappingException("No method for converter " + mapTo.toSchemaMethod());
        } catch (InstantiationException e) {
            throw new InvalidMappingException("Could not create instance of converter " + mapTo.using().getName());
        } catch (InvocationTargetException e) {
            throw new InvalidMappingException("");
        }
    }

    private static boolean usingConverter(MapTo mapTo, boolean fromSchema) {
        String method = fromSchema ? mapTo.fromSchemaMethod() : mapTo.toSchemaMethod();
        if (mapTo.using() != Void.class && method.equals("")) {
            throw new InvalidMappingException("Mapping using converter is not complete for " + mapTo.using().getName());
        }
        return mapTo.using() != Void.class;
    }

    private static boolean checkFieldTypeMatch(Class<? extends Field> source, Class<? extends Field> destination) {
        if (source == destination) {
            return true;
        }
        throw new InvalidMappingException("Source and destination fields do not match");
    }

    private static void setTargetFields(Object schema, Object target) {
        Field[] schemaFields = getSchemaFields(schema);
        for (Field schemaField : schemaFields) {
            MapTo mapTo = getMapToForField(schemaField);
            setTargetField(schema, target, schemaField, mapTo);
        }
    }


    @SuppressWarnings("squid:S2589")
    private static void setTargetField(Object schema, Object target, Field field, MapTo mapTo) {
        if (mapTo != null) {
            try {
                Field targetField = getTargetField(mapTo, target);
                if (mapTo.using() == Void.class && checkFieldTypeMatch(field.getClass(), targetField.getClass())) {
                    setFieldWithValue(target, targetField, field.get(schema));
                }
                if (usingConverter(mapTo, true)) {
                    Method converter = mapTo.using().getMethod(mapTo.fromSchemaMethod(), field.getType());
                    setFieldWithValue(target, targetField, converter.invoke(mapTo.using().newInstance(), field.get(schema)));
                }
            } catch (IllegalAccessException e) {
                throw new InvalidMappingException("Could not access field " + field.getName());
            } catch (NoSuchMethodException e) {
                throw new InvalidMappingException("No method in converter class for " + mapTo.using().getName());
            } catch (InstantiationException e) {
                throw new InvalidMappingException("Could not create instance of converter " + mapTo.using().getName());
            } catch (InvocationTargetException e) {
                throw new InvalidMappingException("Could not invoke converter " + mapTo.using().getName());
            }
        }
    }

    private static Field getTargetField(MapTo mapTo, Object target) {
        try {
            return target.getClass().getDeclaredField(mapTo.fieldName());
        } catch (NoSuchFieldException e) {
            throw new InvalidMappingException("No field with name " + mapTo.fieldName());
        }
    }

    private static MapTo getMapToForField(Field field) {
        field.setAccessible(true);
        return field.getAnnotation(MapTo.class);
    }

    private static Field[] getSchemaFields(Object schema) {
        return schema.getClass().getDeclaredFields();
    }

    private static void setFieldWithValue(Object object, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(object, value);
        } catch (IllegalAccessException e) {
            throw new InvalidMappingException("Could not access " + field.getName());
        }
    }

    private static Class getTargetClass(Object o) {
        Mapping mapping = o.getClass().getAnnotation(Mapping.class);
        return mapping.value();
    }
}
