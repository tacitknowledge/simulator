package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.TestHelper;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author galo
 */
public class
        CsvAdapterTest extends TestCase
{


    private CsvAdapter adapter;

    public void setUp()
    {
        adapter = (CsvAdapter) AdapterFactory.getAdapter(FormatConstants.CSV);
    }

    public void testSuccessfulAdaptFromWithHeaders()
    {
            // --- Provide the required configuration
            // (only CSV_CONTENT is required if using headers)
            Map<String, String> params = new HashMap<String, String>();
            params.put(CsvAdapter.PARAM_CSV_CONTENT, "Words");

            adapter.setParameters(params);

            SimulatorPojo pojo = adapter.createSimulatorPojo(TestHelper.CSV_DATA);

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

    public void testSuccessFullAdaptFromWithoutHeaders() throws FormatAdapterException
    {
            // --- Provide the required configuration
            Map<String, String> params = new HashMap<String, String>();
            params.put(CsvAdapter.PARAM_CSV_CONTENT, "Words");
            params.put(CsvAdapter.PARAM_FIRST_ROW_HEADER, "false");
            params.put(CsvAdapter.PARAM_ROW_CONTENT, "wordSet");

            adapter.setParameters(params);
             adapter.validateParameters();
        
            SimulatorPojo pojo = adapter.createSimulatorPojo(TestHelper.CSV_DATA);

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

    public void testSuccessfulAdaptToWithHeaders()
    {
        try {
            // --- Provide the required configuration
            Map<String, String> params = new HashMap<String, String>();
            params.put(CsvAdapter.PARAM_CSV_CONTENT, "Words");

            adapter.setParameters(params);
            adapter.validateParameters();

            // --- First, get the pojo from the same adapter
            SimulatorPojo pojo = adapter.createSimulatorPojo(TestHelper.CSV_DATA);

            // --- Now, go the other way around
            Object o = adapter.getString(pojo);
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
        } catch(FormatAdapterException e)
        {
            fail("Not expecting exception!");
        }
    }
}
