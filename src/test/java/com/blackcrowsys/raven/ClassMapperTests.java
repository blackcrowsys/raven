package com.blackcrowsys.raven;

import com.blackcrowsys.raven.model.Money;
import com.blackcrowsys.raven.model.Schema;
import com.blackcrowsys.raven.model.Target;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ClassMapperTests {

    private Class clazz;

    private Schema schema;
    private Target target;

    @Before
    public void setUp() {
        clazz = Schema.class;

        schema = new Schema();
        schema.setV1(Integer.valueOf(102));
        schema.setV2("test");

        target = new Target();
        target.setName("test");
        target.setValue(Integer.valueOf(103));
        Money money = new Money();
        money.setCurrency("USD");
        money.setValue(2000L);
        target.setAmount(money);
    }

    @Test
    public void testMappingFromSchemaToEntity() throws InstantiationException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        Target actualTarget = (Target) ClassMapper.mapFromSchema(schema);

        assertEquals(schema.getV1(), actualTarget.getValue());
        assertEquals(schema.getV2(), actualTarget.getName());
        assertNotNull(actualTarget.getAmount());
        assertEquals("GBP", actualTarget.getAmount().getCurrency());
        assertEquals(Long.valueOf(1000L), actualTarget.getAmount().getValue());
    }

    @Test
    public void testMappingFromEntityToSchema() throws InstantiationException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException {
        Schema actualSchema = (Schema) ClassMapper.mapToSchema(Schema.class, target);

        assertNotNull(actualSchema);
        assertTrue(actualSchema instanceof Schema);
        assertEquals(target.getValue(), actualSchema.getV1());
        assertEquals(target.getName(), actualSchema.getV2());
        assertEquals("USD 2000", actualSchema.getAmount());
    }

}
