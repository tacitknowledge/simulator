package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.TestHelper;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class for YamlAdapter
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class YamlAdapterTest extends TestCase
{
    private YamlAdapter adapter;

    public void setUp()
    {
        adapter = (YamlAdapter) AdapterFactory.getAdapter(FormatConstants.YAML);
    }

    public void testAdapterWithoutParameters()
    {
        try
        {
            adapter.generateBeans(TestHelper.YAML_DATA);
            fail("JSON Adapter should throw exception if the required parameters are not provided.");
        }
        catch (FormatAdapterException fae)
        {
            // --- This is ok!
        }
    }

    public void testSuccessfulAdaptFrom()
    {
        // --- Provide the required configuration
        Map<String, String> params = new HashMap<String, String>();
        params.put(YamlAdapter.PARAM_YAML_CONTENT, "employee");
        adapter.setParameters(params);

        try
        {
            SimulatorPojo pojo = adapter.createSimulatorPojo(TestHelper.YAML_DATA);

            assertNotNull(pojo.getRoot().get("employee"));

            Map<String, Object> employee = (Map<String, Object>) pojo.getRoot().get("employee");
            assertEquals("John", employee.get("firstName"));
            assertEquals("Smith", employee.get("lastName"));
            assertNotNull(employee.get("address"));

        }
        catch (FormatAdapterException fae)
        {
            fae.printStackTrace();
            fail("Not expecting exception!");
        }
    }

    public void testSuccessfulAdaptFromSequence() throws FormatAdapterException
    {
        // --- Provide the required configuration
        Map<String, String> params = new HashMap<String, String>();
        params.put(YamlAdapter.PARAM_YAML_CONTENT, "persons");
        params.put(YamlAdapter.PARAM_IS_ARRAY, "true");
        params.put(YamlAdapter.PARAM_YAML_ARRAY_CONTENT, "person");
        adapter.setParameters(params);
        adapter.validateParameters();
        try
        {
            SimulatorPojo pojo = adapter.createSimulatorPojo(TestHelper.YAML_SEQUENCE_DATA);

            assertNotNull(pojo.getRoot().get("persons"));

            Map<String, Object> persons = (Map<String, Object>) pojo.getRoot().get("persons");
            assertNotNull(persons.get("person"));
            assertTrue(persons.get("person") instanceof ArrayList);

            List<Map<String, Object>> personList = (List<Map<String, Object>>) persons.get("person");
            assertEquals(3, personList.size());
            Map<String, Object> person1 = personList.get(0);

            assertEquals("John", person1.get("firstName"));
            assertEquals(40, person1.get("age"));

            Map<String, Object> person3 = personList.get(2);
            assertEquals("Carlson", person3.get("lastName"));
            assertEquals(25, person3.get("age"));

        }
        catch (FormatAdapterException fae)
        {
            fae.printStackTrace();
            fail("Not expecting exception!");
        }
    }
}
