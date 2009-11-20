package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.Transport;
import com.tacitknowledge.simulator.TransportException;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * @author galo
 */
public class JmsTransportTest extends TestCase
{
    /**
     * Params for transport
     */
    private Map<String, String> params;

    /**
     * Setup
     * @throws Exception Anything goes wrong
     */
    @Override
    protected void setUp() throws Exception
    {
        params = new HashMap<String, String>();
    }

    public void testGetUriWithoutParams()
    {
        Transport transport = new JmsTransport();

        assertEquals("jms", transport.getType());

        // --- Try to get the URI
        try
        {
            transport.toUriString();
            fail("Transport should not work without required parameters");
        } catch (TransportException e)
        {
            // --- That's ok
        }
    }

    public void testGetSimplestUri()
    {
        // --- Try to get this URI: jms:foo.bar
        params.put(JmsTransport.PARAM_DESTINATION_NAME, "foo.bar");
        Transport transport = new JmsTransport(params);

        try
        {
            String uri = transport.toUriString();

            assertTrue("Returned uri isn't as expected: " + uri,
                    uri.indexOf("jms:foo.bar") > -1);
        } catch (TransportException e)
        {
            fail("Shouldn't be getting an exception here: " + e.getMessage());
        }
    }

    public void testGetUriForActiveMQTopic()
    {
        // --- Try to get this URI: activemq:topic:foo.bar
        params.put(JmsTransport.PARAM_ACTIVE_MQ, "true");
        params.put(JmsTransport.PARAM_DESTINATION_NAME, "foo.bar");
        params.put(JmsTransport.PARAM_IS_TOPIC, "true");
        Transport transport = new JmsTransport(params);

        try
        {
            String uri = transport.toUriString();

            assertTrue("Returned uri isn't as expected: " + uri,
                    uri.indexOf("activemq:topic:foo.bar") > -1);
        } catch (TransportException e)
        {
            fail("Shouldn't be getting an exception here: " + e.getMessage());
        }
    }
}
