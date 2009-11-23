package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.TestHelper;
import junit.framework.TestCase;
import com.tacitknowledge.simulator.formats.AdapterFactory;
import com.tacitknowledge.simulator.formats.FormatConstants;
import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.FormatAdapterException;

/**
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class XmlAdapterTest extends TestCase
{
    public void testAdaptFromXml()
    {
        Adapter adapter = AdapterFactory.getAdapter(FormatConstants.XML);

        SimulatorPojo pojo;
        try
        {
            // --- Get a SimulatorPojo from our fake little XML
            pojo = adapter.adaptFrom(TestHelper.XML_DATA);

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
        Adapter adapter = AdapterFactory.getAdapter(FormatConstants.XML);

        // --- Lets use the same pojo generated in the adaptFrom() method
        try {
            SimulatorPojo pojo = adapter.adaptFrom(TestHelper.XML_DATA);

            String xml = (String) adapter.adaptTo(pojo);

            // --- Test some nodes, just to make sure the most important things are there
            assertTrue(
                    "Could not find starting en ending employees tags",
                    xml.indexOf("<employees>") > -1 && xml.indexOf("</employees>") > -1);
            assertTrue("Could not find report date element: " + xml,
                    xml.indexOf("<reportDate>2009-11-05</reportDate>") > -1);

            // --- Grab pieces of the XML to test
            int firstEmpIdx = xml.indexOf("<employee>");
            String employee = xml.substring(xml.indexOf("<employee>"), xml.indexOf("</employee>", firstEmpIdx+1) + "</employee>".length());

            assertTrue(employee.indexOf("<name>John</name>") > -1);
            
        } catch(FormatAdapterException e) {
            e.printStackTrace();
            fail("Error trying to adapt from/to XML: " + e.getMessage());
        }
    }
}
