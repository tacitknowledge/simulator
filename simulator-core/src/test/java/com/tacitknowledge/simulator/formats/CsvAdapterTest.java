package com.tacitknowledge.simulator.formats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.DefaultMessage;
import org.junit.Before;
import org.junit.Test;
import org.mozilla.javascript.NativeObject;

import com.tacitknowledge.simulator.BaseConfigurable;
import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.TestHelper;

/**
 * Test class for CvsAdapterTest
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class CsvAdapterTest
{

    private static final String WORDS = "Words";
	private CsvAdapter adapter;

    @Before
    public void setUp()
    {
        adapter = new CsvAdapter();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testSuccessfulAdaptFromWithHeaders()
    {
        // --- Provide the required configuration
        // (only CSV_CONTENT is required if using headers)
        Map<String, String> params = new HashMap<String, String>();
        params.put(CsvAdapter.PARAM_CSV_CONTENT, WORDS);
        BaseConfigurable configurable = new BaseConfigurable();
        configurable.setParameters(params);
        adapter = new CsvAdapter(configurable);

        CamelContext context = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(context);
        Message message = new DefaultMessage();
        message.setBody(TestHelper.CSV_DATA);
        exchange.setIn(message);

        SimulatorPojo pojo = adapter.createSimulatorPojo(exchange);

        Object o = pojo.getRoot().get(WORDS);
        // --- First, make sure we got the root Map with a Words key
        assertNotNull(pojo.getRoot().get(WORDS));
        // --- Next, check that it's a List
        assertTrue(o instanceof List);

        List list = (List) o;
        assertEquals(2, list.size());
        assertTrue(list.get(0) instanceof Map);

        Map<String, String> row = (Map<String, String>) list.get(0);
        assertEquals("el", row.get("tercero"));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testSuccessFullAdaptFromWithoutHeaders()
            throws FormatAdapterException, ConfigurableException
    {
        // --- Provide the required configuration
        Map<String, String> params = new HashMap<String, String>();
        params.put(CsvAdapter.PARAM_CSV_CONTENT, WORDS);
        params.put(CsvAdapter.PARAM_FIRST_ROW_HEADER, "false");
        params.put(CsvAdapter.PARAM_ROW_CONTENT, "wordSet");
        BaseConfigurable configurable = new BaseConfigurable();
        configurable.setParameters(params);
        adapter = new CsvAdapter(configurable);
        adapter.validateParameters();

        CamelContext context = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(context);
        Message message = new DefaultMessage();
        message.setBody(TestHelper.CSV_DATA);
        exchange.setIn(message);

        SimulatorPojo pojo = adapter.createSimulatorPojo(exchange);

        Object o = pojo.getRoot().get(WORDS);
        // --- First, make sure we got the root Map with a Words key
        assertNotNull(pojo.getRoot().get(WORDS));
        // --- Next, check that it's a List
        assertTrue(o instanceof List);

        List list = (List) o;
        assertEquals(3, list.size());
        assertTrue(list.get(0) instanceof Map);

        Map<String, List> row = (Map<String, List>) list.get(0);
        assertNotNull(row.get("wordSet"));
        assertEquals("primero", row.get("wordSet").get(0));

    }

    @Test
    public void testSuccessfulAdaptToWithHeaders()
    {
        try
        {
            // --- Provide the required configuration
            Map<String, String> params = new HashMap<String, String>();
            params.put(CsvAdapter.PARAM_CSV_CONTENT, WORDS);
            BaseConfigurable configurable = new BaseConfigurable();
            configurable.setParameters(params);
            adapter = new CsvAdapter(configurable);
            adapter.validateParameters();

            CamelContext context = new DefaultCamelContext();
            Exchange exchange = new DefaultExchange(context);
            Message message = new DefaultMessage();
            message.setBody(TestHelper.CSV_DATA);
            exchange.setIn(message);

            // --- First, get the pojo from the same adapter
			Map map = adapter.adaptForInput(exchange);

			// --- Now, go the other way around

			String csvData = (String) adapter.adaptToOutput(map.get(WORDS),
			        exchange);

            // --- Now test the returned data contents
            assertTrue(csvData.indexOf("primero") > -1);
            Pattern p = Pattern.compile("$", Pattern.MULTILINE);
            String[] rows = p.split(csvData);

            assertEquals(3, rows.length);
            // --- Figure out which column ended up being "primero"
            String headersRow = rows[0];
            List<String> headers = Arrays.asList(headersRow.trim().split(","));
            int primeroIdx = headers.indexOf("primero");

            // --- Now verify that "yo" and "hoy" are in the same column as "primero"
            List<String> row1 = Arrays.asList(rows[1].trim().split(","));
            List<String> row2 = Arrays.asList(rows[2].trim().split(","));

            assertEquals(primeroIdx, row1.indexOf("yo"));
            assertEquals(primeroIdx, row2.indexOf("hoy"));
		}
        catch (Exception e)
        {
            fail("Not expecting exception!");
        }
    }

	@Test
	public void testSuccessfulAdaptToWithoutHeaders() {
		try {
			// --- Provide the required configuration
			Map<String, String> params = new HashMap<String, String>();
			params.put(CsvAdapter.PARAM_CSV_CONTENT, WORDS);
			params.put(CsvAdapter.PARAM_FIRST_ROW_HEADER, "false");
			params.put(CsvAdapter.PARAM_ROW_CONTENT, "1, 2, 3, 4, 5, 6");

			BaseConfigurable configurable = new BaseConfigurable();
			configurable.setParameters(params);
			adapter = new CsvAdapter(configurable);
			adapter.validateParameters();

			CamelContext context = new DefaultCamelContext();
			Exchange exchange = new DefaultExchange(context);
			Message message = new DefaultMessage();
			message.setBody(TestHelper.CSV_DATA);
			exchange.setIn(message);
			Map map = adapter.adaptForInput(exchange);

			// --- Now, go the other way around
			/*
			 * NativeObject native1 = getNativeObject("primero", "yo",
			 * "secundo", "yo1"); NativeObject native2 =
			 * getNativeObject("primero", "hoy", "secundo", "hoy1");
			 *
			 * NativeArray multiline = new NativeArray(new Object[] { native1,
			 * native2 });
			 */

			String csvData = (String) adapter
.adaptToOutput(map.get(WORDS),
			        exchange);

			// --- Now test the returned data contents
			assertTrue(csvData.indexOf("primero") > -1);
			Pattern p = Pattern.compile("$", Pattern.MULTILINE);
			String[] rows = p.split(csvData);

			assertEquals(3, rows.length);
			// --- Figure out which column ended up being "primero"
			String headersRow = rows[0];
			List<String> headers = Arrays.asList(headersRow.trim().split(","));
			int primeroIdx = headers.indexOf("primero");

			// --- Now verify that "yo" and "hoy" are in the same column as
			// "primero"
			List<String> row1 = Arrays.asList(rows[1].trim().split(","));
			List<String> row2 = Arrays.asList(rows[2].trim().split(","));

			assertEquals(primeroIdx, row1.indexOf("yo"));
			assertEquals(primeroIdx, row2.indexOf("hoy"));
		} catch (Exception e) {
			fail("Not expecting exception!");
		}
	}

	private NativeObject getNativeObject(String key1, String value1,
	        String key2,
	        String value2) {
		NativeObject object1 = new NativeObject();
		NativeObject.defineProperty(object1, key1, value1, 0);
		NativeObject.defineProperty(object1, key2, value2, 0);

		return object1;
	}
}
