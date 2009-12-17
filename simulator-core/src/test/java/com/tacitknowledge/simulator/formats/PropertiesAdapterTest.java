package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.TestHelper;
import junit.framework.TestCase;

import java.util.Map;

/**
 * Test class for PropertiesAdapter
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class PropertiesAdapterTest extends TestCase
{
    private PropertiesAdapter adapter;

    public void setUp()
    {
        adapter = new PropertiesAdapter();
    }

    public void testSuccessfulAdaptFrom()
    {
        try
        {
            SimulatorPojo pojo = adapter.createSimulatorPojo(TestHelper.PROPERTIES_DATA);

            assertNotNull(pojo.getRoot().get("employee"));

            Map<String, Object> employee = (Map<String, Object>) pojo.getRoot().get("employee");

            assertEquals("John", employee.get("firstName"));
            assertEquals("Smith", employee.get("lastName"));
            assertEquals("Manager", employee.get("title"));
            assertNotNull(employee.get("address"));

            Map<String, Object> emp_address = (Map<String, Object>) employee.get("address");
            assertEquals("21 2nd Street", emp_address.get("streetAddress"));
            assertEquals("New York", emp_address.get("city"));
            assertEquals("NY", emp_address.get("state"));

        } catch(FormatAdapterException fae)
        {
            fae.printStackTrace();
            fail("Not expecting exception!");
        }
    }

    public void testSuccessfulAdaptTo()
    {
        try
        {
            SimulatorPojo pojo = adapter.createSimulatorPojo(TestHelper.PROPERTIES_DATA);

            assertNotNull(pojo.getRoot().get("employee"));

            // --- Now,. do the inverse
            String props = adapter.getString(pojo);

            assertTrue(props.contains("employee.firstName=John"));
            assertTrue(props.contains("employee.title=Manager"));
            assertTrue(props.contains("employee.address.streetAddress=21 2nd Street"));
        } catch (FormatAdapterException fae)
        {
            fae.printStackTrace();
            fail("Nor expecting exception!");
        }
    }
}
