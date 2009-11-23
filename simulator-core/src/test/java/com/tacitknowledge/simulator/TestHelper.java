package com.tacitknowledge.simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author galo
 */
public abstract class TestHelper
{
    /**
     * Simple XML data for testing
     */
    public final static String XML_DATA =
            "<employees>" +
                    " <employee>" +
                    "   <name>John</name>" +
                    "   <title>Manager</title>" +
                    " </employee>" +
                    " <employee>" +
                    "   <name>Sara</name>" +
                    "   <title>Clerk</title>" +
                    " </employee>" +
                    " <reportDate>2009-11-05</reportDate>" +
                    " <roles>" +
                    "   <role>Clerk</role>" +
                    "   <role>Manager</role>" +
                    "   <role>Accountant</role>" +
                    " </roles>" +
                    "</employees>";

    /**
     * Default item fields
     */
    public final static String[] ITEM_FIELDS = {"sku", "quantity", "price"};

    /**
     * Creates and populates a SimulatorPojo for consumption of the PojoClassGenerator.
     * PojoClassGenerator should generate JavaBeans from the Maps inside the SimulatorPojo
     *
     * @return The generated SimulatorPojo
     */
    public static SimulatorPojo createOrderSimulatorPojo()
    {
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
        Map<String, String> item1 = generateMap(ITEM_FIELDS, new String[]{"1234567", "1", "29.90"});
        Map<String, String> item2 = generateMap(ITEM_FIELDS, new String[]{"0987654", "3", "9.99"});
        List<Map> item = new ArrayList<Map>();
        item.add(item1);
        item.add(item2);

        // --- Items will be an Map with a List attribute item
        Map<String, Object> items = new HashMap<String, Object>();
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
     * Easily generate a Map from the list of fields and values.
     * Useful for SimulatorPojo generation
     *
     * @param fields The array of fields to create the Map
     * @param values The array of values to create the Map.
     * @return The generated Map
     */
    public static Map<String, String> generateMap(String[] fields, String[] values)
    {
        Map<String, String> item = new HashMap<String, String>();
        for (int i = 0; i < ITEM_FIELDS.length; i++)
        {
            item.put(ITEM_FIELDS[i], values[i]);
        }
        return item;
    }
}
