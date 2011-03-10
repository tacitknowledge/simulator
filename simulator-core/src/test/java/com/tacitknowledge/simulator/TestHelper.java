package com.tacitknowledge.simulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test Helper abstract class
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public abstract class TestHelper
{
    public static final String RESOURCES_PATH = "src/test/resources/";
    public static final String ORIGINAL_FILES_PATH = RESOURCES_PATH + "original_files/";

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

    public final static String JSON_DATA =
        "{" +
            "'firstName': 'John'," +
            "'lastName': 'Smith'," +
            "'address': {" +
            "'streetAddress': '21 2nd Street'," +
            "'city': 'New York'," +
            "'state': 'NY'," +
            "'postalCode': 10021" +
            "}," +
            "'phoneNumbers': [" +
            "['home', '212 732-1234']," +
            "['mobile', '646 123-4567']" +
            "]" +
            "}";

    public final static String JSON_DATA_ARRAY =
        "[" +
            "[1,2,3,4,5]," +
            "['a', 'b', 'c', 'd', 'e']," +
            "['alpha', 'beta', 'gamma', 'delta', 'eta']" +
            "]";

    /**
     * Default item fields
     */
    public final static String[] ITEM_FIELDS = {"sku", "quantity", "price"};

    /**
     * Default CSV test data
     */
    public final static String CSV_DATA =
        "primero, segundo, tercero, cuarto, quinto, sexto\n" +
            "yo,tu,el,nosotros,ustedes,ellos\n" +
            "hoy,manana,ayer,pasado manana,antier,proximo mes";

    /**
     * Default PROPERTIES test data
     */
    public final static String PROPERTIES_DATA =
        "employee.firstName=John\n" +
            "employee.lastName=Smith\n" +
            "employee.address.streetAddress=21 2nd Street\n" +
            "employee.address.city=New York\n" +
            "employee.address.state=NY\n" +
            "employee.title=Manager";

    /**
     * Default YAML test data
     */
    public final static String YAML_DATA =
        "---\n" +
            "firstName: John\n" +
            "lastName: Smith\n" +
            "address:\n" +
            "    streetAddress: 21 2nd Street\n" +
            "    city: New York\n" +
            "    state: NY\n" +
            "title: Manager\n" +
            "expirationDate: 12/21/2012";

    public final static String YAML_SEQUENCE_DATA =
        "- firstName: John\n" +
            "  lastName: Smith\n" +
            "  age: 40\n" +
            "- firstName: Sara\n" +
            "  lastName: Jameson\n" +
            "  age: 28\n" +
            "- firstName: Mary\n" +
            "  lastName: Carlson\n" +
            "  age: 25";

    /**
     * Creates and populates a SimulatorPojo for consumption of the PojoClassGenerator.
     * PojoClassGenerator should generate JavaBeans from the Maps inside the SimulatorPojo
     *
     * @return The generated SimulatorPojo
     */
    @SuppressWarnings("rawtypes")
    public static SimulatorPojo createOrderSimulatorPojo()
    {
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

        SimulatorPojo pojo = new StructuredSimulatorPojo();
        pojo.getRoot().put("order", order);

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

    /**
     *
     * @param filePathName The path and name of the file to be read
     * @return The file contents as a String
     * @throws Exception If anything goes wrong
     */
    public static String readFile(String filePathName)
            throws Exception
    {
        return readFile(new File(filePathName));
    }

    /**
     *
     * @param file The File to be read
     * @return The file contents as a String
     * @throws Exception If anything goes wrong
     */
    public static String readFile(File file)
            throws Exception
    {
        // --- Make sure file1 exists
        if (!file.exists())
        {
            throw new Exception("Original file must exist: " + file.getAbsolutePath());
        }

        InputStream is = new FileInputStream(file);
        StringBuilder sb = new StringBuilder();
        String line;

        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            
            while ((line = reader.readLine()) != null)
            {
                sb.append(line).append("\n");
            }
            
            return sb.toString();
        }
        finally
        {
            is.close();
        }
    }

    public static void copyFile(File file1, File file2)
        throws Exception
    {
        // --- Make sure file1 exists
        if (!file1.exists())
        {
            throw new Exception("Original file must exist: " + file1.getAbsolutePath());
        }

        InputStream in = new FileInputStream(file1);
        OutputStream out = new FileOutputStream(file2);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0)
        {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
        System.out.println("File copied to " + file2.getAbsolutePath());
    }

    public static Map<String, Object> getMapOneEntry(String entryName, Object entryValue)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(entryName, entryValue);
        return map;
    }
}
