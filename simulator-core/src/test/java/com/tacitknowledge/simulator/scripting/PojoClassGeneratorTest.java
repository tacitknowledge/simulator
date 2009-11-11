package com.tacitknowledge.simulator.scripting;

import junit.framework.TestCase;
import javassist.ClassPool;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.lang.reflect.Field;
import java.lang.reflect.Array;

import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.StructuredSimulatorPojo;

/**
 * @author galo
 */
public class PojoClassGeneratorTest extends TestCase
{
    private final static String[] ITEM_FIELDS = {"sku", "quantity", "price"};

    static ClassPool pool = new ClassPool(true);
    static PojoClassGenerator generator = new PojoClassGenerator(pool);

    public void testNoGeneratedClasses() {
        // ---
        List<String> list = generator.getGeneratedClassNames();

        assertEquals(0, list.size());
    }

    public void testGeneratedClasses() {
        // --- Send a SimulatorPojo and make sure it generates the expected classes
        SimulatorPojo pojo = createSimulatorPojo();

        // --- First, check the first generated class (the Order class) doesn't exist
        try {
            Class.forName(generator.getGeneratedClassesPackage() + "Order");
            fail("Order dinamically-generated class shouldn't exist");
        } catch(ClassNotFoundException e) {
            // --- That's ok
        }

        try {
            generator.generateBeansMap(pojo);
        } catch(Exception e)
        {
            e.printStackTrace();
            fail("Error trying to generate beans map: " + e.getMessage());
        }

        // --- Now, make sure we got a GENERATED_CLASSES_PACKAGE + Order class
        try {
            Class.forName(generator.getGeneratedClassesPackage() + "Order");
            Class.forName(generator.getGeneratedClassesPackage() + "Order.Items");
            Class.forName(generator.getGeneratedClassesPackage() + "Order.Items.Item");
            Class.forName(generator.getGeneratedClassesPackage() + "Order.Shippinginfo");
        } catch(ClassNotFoundException e) {
            fail("Generated class should have been found: " + e.getMessage());
        }
    }

    public void testGeneratedBeans() {
        SimulatorPojo pojo = createSimulatorPojo();

        // --- Generate beans
        Map<String,Object> beans = null;
        try {
             beans = generator.generateBeansMap(pojo);
        } catch(Exception e)
        {
            e.printStackTrace();
            fail("Error trying to generate beans map: " + e.getMessage());
        }

        // --- Test that we got an Order instance
        Object order = beans.get("order");
        assertNotNull(order);
        try {
            assertEquals(Class.forName(generator.getGeneratedClassesPackage() + "Order"), order.getClass());

            // --- Test we got status and items fields
            Field statusField = order.getClass().getField("status");
            Field itemsField = order.getClass().getField("items");
            // --- Test the fields values
            assertEquals("100", statusField.get(order));
            Object items = itemsField.get(order);
            assertNotNull(items);
            assertEquals(Class.forName(generator.getGeneratedClassesPackage() + "Order.Items"), items.getClass());

            // --- Go down, test that Items has an item field and that it's an array of Item beans
            String itemClassName = generator.getGeneratedClassesPackage() + "Order.Items.Item";
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
        } catch(Exception e) {
            e.printStackTrace();
            fail("Failed to validate classes and fields through reflection: " + e.getMessage());
        }
    }


    /**
     * Creates and populates a SimulatorPojo for consumption of the PojoClassGenerator.
     * PojoClassGenerator should generate JavaBeans from the Maps inside the SimulatorPojo
     * @return The generated SimulatorPojo
     */
    private SimulatorPojo createSimulatorPojo() {
        Map<String, Object> root = new HashMap<String, Object>();

        /*
            For this particular test, we're going to emulate an Order.
            The Order will have id, an order date, status, a list of items and shipping info.
            Items will have sku, quantity, price.
            ShippingInfo will have address, city, state and zip code
         */

        // --- Let's start with the order and its basic attributes
        Map<String, Object> order = new HashMap<String, Object>();
        order.put("id", "24680");
        order.put("orderDate", "2009-11-11");
        order.put("status", "100");

        // --- Next, items
        Map<String, String> item1 = generateItemMap(new String[] {"1234567", "1", "29.90"});
        Map<String, String> item2 = generateItemMap(new String[] {"0987654", "3", "9.99"});
        List<Map> item = new ArrayList<Map>();
        item.add(item1);
        item.add(item2);

        // --- Items will be an Map with a List attribute item
        Map<String,Object> items = new HashMap<String,Object>();
        items.put("item", item);
        order.put("items", items);

        // --- Next, Shipping info
        Map<String, String> shipInfo = new HashMap<String, String>();
        shipInfo.put("address", "123 Oak Road");
        shipInfo.put("city", "Springfield");
        shipInfo.put("state", "CO");
        shipInfo.put("zipCode", "81073");
        order.put("shippingInfo", shipInfo);

        root.put("order", order);

        SimulatorPojo pojo = new StructuredSimulatorPojo();
        pojo.setRoot(root);

        return pojo;
    }

    /**
     * Easily generate an Item Map
     * @param values The array of values to create the item. Fields are defined in #ITEM_FIELDS
     * @return The generated Item Map
     */
    private Map<String,String> generateItemMap(String[] values)
    {
        Map<String,String> item = new HashMap<String,String>();
        for (int i = 0; i < ITEM_FIELDS.length; i++) {
            item.put(ITEM_FIELDS[i], values[i]);
        }
        return item;
    }
}
