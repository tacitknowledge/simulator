package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.TestHelper;
import junit.framework.TestCase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.DefaultMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class for JsonAdapterTest
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class JsonAdapterTest extends TestCase
{
    private JsonAdapter adapter;

    public void setUp()
    {
        adapter = new JsonAdapter();
    }

    public void testAdapterWithoutParameters()
    {
        try
        {
            CamelContext context = new DefaultCamelContext();
            Exchange exchange = new DefaultExchange(context);
            Message message = new DefaultMessage();
            message.setBody(TestHelper.JSON_DATA);
            exchange.setIn(message);
            adapter.generateBeans(exchange);
            fail("JSON Adapter should throw exception if the required parameters are not provided.");
        }
        catch (Exception fae)
        {
            // --- This is ok!
        }
    }

    public void testSuccessfulAdaptFrom()
    {
        // --- Provide required configuration
        Map<String, String> params = new HashMap<String, String>();
        params.put(JsonAdapter.PARAM_JSON_CONTENT, "person");
        adapter.setParameters(params);

        try
        {
            CamelContext context = new DefaultCamelContext();
            Exchange exchange = new DefaultExchange(context);
            Message message = new DefaultMessage();
            message.setBody(TestHelper.JSON_DATA);
            exchange.setIn(message);

            SimulatorPojo pojo = adapter.createSimulatorPojo(exchange);

            assertNotNull(pojo.getRoot().get("person"));
            Map<String, Object> person = (Map<String, Object>) pojo.getRoot().get("person");
            // --- First name
            assertEquals("John", person.get("firstName"));
            // --- Address
            assertNotNull(person.get("address"));
            Map<String, Object> address = (Map<String, Object>) person.get("address");
            assertEquals("21 2nd Street", address.get("streetAddress"));
            assertEquals("10021", address.get("postalCode"));

            // --- Phone numbers
            assertNotNull(person.get("phoneNumbers"));
            // --- Phone numbers should be a List with 2 elements
            List phoneNumbers = (List) person.get("phoneNumbers");
            assertEquals(2, phoneNumbers.size());
            // --- The list elements must be ArrayLists themselves
            assertEquals(ArrayList.class, phoneNumbers.get(0).getClass());
            List<String> home = (List<String>) phoneNumbers.get(0);
            assertEquals("home", home.get(0));
            assertEquals("212 732-1234", home.get(1));
        }
        catch (FormatAdapterException e)
        {
            e.printStackTrace();
            fail("Not expecting exception!");
        }
    }

    public void testSuccessfulAdaptTo()
    {
        // ---
        // --- Provide required configuration
        Map<String, String> params = new HashMap<String, String>();
        params.put(JsonAdapter.PARAM_JSON_CONTENT, "person");
        adapter.setParameters(params);

        try
        {
            CamelContext context = new DefaultCamelContext();
            Exchange exchange = new DefaultExchange(context);
            Message message = new DefaultMessage();
            message.setBody(TestHelper.JSON_DATA);
            exchange.setIn(message);

            // --- Use the same data to get a pojo first
            SimulatorPojo pojo = adapter.createSimulatorPojo(exchange);

            // --- Now, use the same pojo to generate a JSON string
            String jsonString = (String) adapter.getString(pojo, exchange);

            // --- Use the JSON parsers to compare the original and the output
            JSONObject original = new JSONObject(TestHelper.JSON_DATA);
            JSONObject generated = new JSONObject(jsonString);

            assertEquals(original.getString("firstName"), generated.getString("firstName"));
            assertEquals(original.getString("lastName"), generated.getString("lastName"));

            assertEquals(
                original.getJSONArray("phoneNumbers").getJSONArray(0).getString(1),
                generated.getJSONArray("phoneNumbers").getJSONArray(0).getString(1));

            JSONObject originalAddress = original.getJSONObject("address");
            JSONObject generatedAddress = generated.getJSONObject("address");
            assertEquals(
                originalAddress.getString("streetAddress"),
                generatedAddress.getString("streetAddress"));
        }
        catch (FormatAdapterException e)
        {
            e.printStackTrace();
            fail("Not expecting exception!");
        }
        catch (JSONException je)
        {
            je.printStackTrace();
            fail("Not expecting exception!");
        }
    }

    public void testSuccessfulAdaptToWithOnlyArrays()
            throws FormatAdapterException, JSONException, ConfigurableException
    {
        // ---
        // --- Provide required configuration
        Map<String, String> params = new HashMap<String, String>();
        params.put(JsonAdapter.PARAM_JSON_CONTENT, "stuff");
        params.put(JsonAdapter.PARAM_IS_ARRAY, "true");
        params.put(JsonAdapter.PARAM_JSON_ARRAY_CONTENT, "anArray");
        adapter.setParameters(params);
        adapter.validateParameters();

        CamelContext context = new DefaultCamelContext();
            Exchange exchange = new DefaultExchange(context);
            Message message = new DefaultMessage();
            message.setBody(TestHelper.JSON_DATA_ARRAY);
            exchange.setIn(message);

        // ---
        // --- Use JSON arrays data to get a pojo first
        SimulatorPojo pojo = adapter.createSimulatorPojo(exchange);

        // --- Now, use the same pojo to generate a JSON string
        String jsonString = (String) adapter.getString(pojo, exchange);

        // --- Make sure string starts and ends with square brackets
        assertEquals(0, jsonString.indexOf("["));
        assertEquals(jsonString.length() - 1, jsonString.lastIndexOf("]"));

        // --- Need to compare returned values by indexes
        JSONArray original = new JSONArray(TestHelper.JSON_DATA_ARRAY);
        JSONArray generated = new JSONArray(jsonString);

        assertEquals(original.length(), generated.length());

        JSONArray originalItem1 = original.getJSONArray(0);
        JSONArray generatedItem1 = generated.getJSONArray(0);

        assertEquals(originalItem1.length(), generatedItem1.length());
        assertEquals(originalItem1.getString(0), generatedItem1.getString(0));

    }
}
