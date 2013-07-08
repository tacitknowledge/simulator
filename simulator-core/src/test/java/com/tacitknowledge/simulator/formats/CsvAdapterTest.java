package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.*;

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
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test class for CvsAdapterTest
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class CsvAdapterTest
{

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
        params.put(CsvAdapter.PARAM_CSV_CONTENT, "Words");
        BaseConfigurable configurable = new BaseConfigurable();
        configurable.setParameters(params);
        adapter = new CsvAdapter(configurable);

        CamelContext context = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(context);
        Message message = new DefaultMessage();
        message.setBody(TestHelper.CSV_DATA);
        exchange.setIn(message);

        SimulatorPojo pojo = adapter.createSimulatorPojo(exchange);

        Object o = pojo.getRoot().get("Words");
        // --- First, make sure we got the root Map with a Words key
        assertNotNull(pojo.getRoot().get("Words"));
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
        params.put(CsvAdapter.PARAM_CSV_CONTENT, "Words");
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

        Object o = pojo.getRoot().get("Words");
        // --- First, make sure we got the root Map with a Words key
        assertNotNull(pojo.getRoot().get("Words"));
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
            params.put(CsvAdapter.PARAM_CSV_CONTENT, "Words");
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
            SimulatorPojo pojo = adapter.createSimulatorPojo(exchange);

            // --- Now, go the other way around
            Object o = (String) adapter.getString(pojo, exchange);
            assertTrue(o instanceof String);

            String csvData = (String) o;

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
}
