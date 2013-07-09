package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.TestHelper;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.DefaultMessage;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;

/**
 * Test class for PropertiesAdapter
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class PropertiesAdapterTest
{
    private PropertiesAdapter adapter;

    @Before
    public void setUp()
    {
        adapter = new PropertiesAdapter();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSuccessfulAdaptFrom()
    {
        try
        {
            CamelContext context = new DefaultCamelContext();
            Exchange exchange = new DefaultExchange(context);
            Message message = new DefaultMessage();
            message.setBody(TestHelper.PROPERTIES_DATA);
            exchange.setIn(message);
            SimulatorPojo pojo = adapter.createSimulatorPojo(exchange);

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

        }
        catch (FormatAdapterException fae)
        {
            fae.printStackTrace();
            fail("Not expecting exception!");
        }
    }

    @Test
    public void testSuccessfulAdaptTo()
    {
        try
        {
            CamelContext context = new DefaultCamelContext();
            Exchange exchange = new DefaultExchange(context);
            Message message = new DefaultMessage();
            message.setBody(TestHelper.PROPERTIES_DATA);
            exchange.setIn(message);
            SimulatorPojo pojo = adapter.createSimulatorPojo(exchange);

            assertNotNull(pojo.getRoot().get("employee"));

            // --- Now,. do the inverse
            String props = (String) adapter.getConversationResponseAsString(pojo, exchange);

            assertTrue(props.contains("employee.firstName=John"));
            assertTrue(props.contains("employee.title=Manager"));
            assertTrue(props.contains("employee.address.streetAddress=21 2nd Street"));
        }
        catch (FormatAdapterException fae)
        {
            fae.printStackTrace();
            fail("Nor expecting exception!");
        }
    }
}
