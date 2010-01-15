package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.TestHelper;
import junit.framework.TestCase;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.DefaultMessage;

/**
 * Test class for XmlAdapter
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class XmlAdapterTest extends TestCase
{
    public void testAdaptFromXml()
    {
        XmlAdapter adapter = (XmlAdapter) AdapterFactory.getInstance().getAdapter(FormatConstants.XML);

        SimulatorPojo pojo;
        try
        {
            CamelContext context = new DefaultCamelContext();
            Exchange exchange = new DefaultExchange(context);
            Message message = new DefaultMessage();
            message.setBody(TestHelper.XML_DATA);
            exchange.setIn(message);
            // --- Get a SimulatorPojo from our fake little XML
            pojo = adapter.createSimulatorPojo(exchange);

            // --- Assert the pojo has a root
            assertNotNull(pojo.getRoot());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testAdaptToXml()
    {
        XmlAdapter adapter = (XmlAdapter) AdapterFactory.getInstance().getAdapter(FormatConstants.XML);

        // --- Lets use the same pojo generated in the generateBeans() method
        try
        {
            CamelContext context = new DefaultCamelContext();
            Exchange exchange = new DefaultExchange(context);
            Message message = new DefaultMessage();
            message.setBody(TestHelper.XML_DATA);
            exchange.setIn(message);

            SimulatorPojo pojo = adapter.createSimulatorPojo(exchange);

            String xml = (String) adapter.getString(pojo, exchange);

            // --- Test some nodes, just to make sure the most important things are there
            assertTrue(
                "Could not find starting en ending employees tags",
                xml.indexOf("<employees>") > -1 && xml.indexOf("</employees>") > -1);
            assertTrue("Could not find report date element: " + xml,
                xml.indexOf("<reportDate>2009-11-05</reportDate>") > -1);

            // --- Grab pieces of the XML to test
            int firstEmpIdx = xml.indexOf("<employee>");
            String employee = xml.substring(xml.indexOf("<employee>"), xml.indexOf("</employee>", firstEmpIdx + 1) + "</employee>".length());

            assertTrue(employee.indexOf("<name>John</name>") > -1);

        }
        catch (FormatAdapterException e)
        {
            e.printStackTrace();
            fail("Error trying to adapt from/to XML: " + e.getMessage());
        }
    }
}
