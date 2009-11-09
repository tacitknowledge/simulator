package com.tacitknowledge.simulator;

import junit.framework.TestCase;
import com.tacitknowledge.simulator.formats.AdapterFactory;

/**
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class XmlAdapterTest extends TestCase
{
    public void testAdaptFromXml()
    {
        String data =
                "<employees>" +
                        " <employee>" +
                        "   <name>John</name>" +
                        "   <title>Manager</title>" +
                        " </employee>" +
                        " <employee>" +
                        "   <name>Sara</name>" +
                        "   <title>Clerk</title>" +
                        " </employee>" +
                        " <report-date>2009-11-05</report-date>" +
                        " <roles>" +
                        "   <role>Clerk</role>" +
                        "   <role>Manager</role>" +
                        "   <role>Accountant</role>" +
                        " </roles>" +
                        "</employees>";

        Adapter adapter = AdapterFactory.getAdapter(FormatConstants.XML);

        SimulatorPojo pojo;
        try
        {
            pojo = adapter.adaptFrom(data);

            // --- Assert the pojo has a root
            assertNotNull(pojo.getRoot());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }
}
