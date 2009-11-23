package com.tacitknowledge.simulator.scripting;

import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.TestHelper;
import javassist.ClassPool;
import junit.framework.TestCase;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * @author galo
 */
public class PojoClassGeneratorTest extends TestCase
{
    private final static String[] ITEM_FIELDS = {"sku", "quantity", "price"};

    static ClassPool pool = new ClassPool(true);
    static PojoClassGenerator generator = new PojoClassGenerator(pool);

    public void testNoGeneratedClasses()
    {
        // ---
        List<String> list = generator.getGeneratedClassNames();

        assertEquals(0, list.size());
    }

    public void testGeneratedClasses()
    {
        // --- Send a SimulatorPojo and make sure it generates the expected classes
        SimulatorPojo pojo = TestHelper.createOrderSimulatorPojo();

        // --- First, check the first generated class (the Order class) doesn't exist
        try
        {
            Class.forName(generator.getGeneratedClassesPackage() + "Order");
            fail("Order dinamically-generated class shouldn't exist");
        }
        catch (ClassNotFoundException e)
        {
            // --- That's ok
        }

        try
        {
            generator.generateBeansMap(pojo);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Error trying to generate beans map: " + e.getMessage());
        }

        // --- Now, make sure we got a GENERATED_CLASSES_PACKAGE + Order class
        try
        {
            String realName =
                    generator.getRealGeneratedClassName(
                            generator.getGeneratedClassesPackage() + "Order");
            Class.forName(realName);

            realName = generator.getRealGeneratedClassName(
                            generator.getGeneratedClassesPackage() + "Order.Items");
            Class.forName(realName);

            realName = generator.getRealGeneratedClassName(
                            generator.getGeneratedClassesPackage() + "Order.Items.Item");
            Class.forName(realName);

            realName = generator.getRealGeneratedClassName(
                            generator.getGeneratedClassesPackage() + "Order.Shippinginfo");
            Class.forName(realName);
        }
        catch (ClassNotFoundException e)
        {
            fail("Generated class should have been found: " + e.getMessage());
        }
    }

    public void testGeneratedBeans()
    {
        SimulatorPojo pojo = TestHelper.createOrderSimulatorPojo();

        // --- Generate beans
        Map<String, Object> beans = null;
        try
        {
            beans = generator.generateBeansMap(pojo);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Error trying to generate beans map: " + e.getMessage());
        }

        // --- Test that we got an Order instance
        Object order = beans.get("order");
        assertNotNull(order);
        try
        {
            // --- We need to know the actual class name
            String realName =
                    generator.getRealGeneratedClassName(
                            generator.getGeneratedClassesPackage() + "Order");
            assertEquals(Class.forName(realName), order.getClass());

            // --- Test we got status and items fields
            Field statusField = order.getClass().getField("status");
            Field itemsField = order.getClass().getField("items");
            // --- Test the fields values
            assertEquals("100", statusField.get(order));
            Object items = itemsField.get(order);
            assertNotNull(items);

            realName = generator.getRealGeneratedClassName(
                            generator.getGeneratedClassesPackage() + "Order.Items");
            assertEquals(Class.forName(realName), items.getClass());

            // --- Go down, test that Items has an item field and that it's an array of Item beans
            String itemClassName = generator.getRealGeneratedClassName(
                            generator.getGeneratedClassesPackage() + "Order.Items.Item");
            Class.forName(itemClassName);

            Field itemField = items.getClass().getField("item");
            Object itemArray = itemField.get(items);
            assertNotNull(itemArray);
            assertEquals(Array.newInstance(Class.forName(itemClassName), 0).getClass(), itemArray.getClass());
            assertEquals(Array.getLength(itemArray), 2);
            // --- Test the first item contents
            Object item1 = Array.get(itemArray, 0);
            Field idField = item1.getClass().getField(ITEM_FIELDS[0]);
            assertEquals(String.class, idField.getType());
            assertEquals("1234567", idField.get(item1));

            // --- Get rid of the generator and pool references
            generator = null;
            pool = null;

            // --- 
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Failed to validate classes and fields through reflection: " + e.getMessage());
        }
    }
}
