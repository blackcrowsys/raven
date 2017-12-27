package com.blackcrowsys.raven;

import com.blackcrowsys.raven.model.Schema;
import com.blackcrowsys.raven.model.Target;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ClassMapperTests {

    private Class clazz;

    private Schema schema;

    @Before
    public void setUp(){
         clazz = Schema.class;

         schema = new Schema();
         schema.setV1(Integer.valueOf(102));
    }

    @Test
    public void testMappingFromSchemaToEntity() throws InstantiationException, IllegalAccessException {
        Object target = ClassMapper.mapFromSchema(schema);
        assertNotNull(target);
        assertTrue(target instanceof Target);
    }

    @Test
    public void testMappingIntegerFromSchemaToTarget() throws InstantiationException, IllegalAccessException {
        Target target = (Target) ClassMapper.mapFromSchema(schema);

        assertEquals(schema.getV1(), target.getValue());
    }
}
