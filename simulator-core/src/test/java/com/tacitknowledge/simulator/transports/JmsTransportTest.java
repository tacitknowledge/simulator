package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.BaseConfigurable;
import com.tacitknowledge.simulator.Transport;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;

/**
 * Test class for JmsTransport
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class JmsTransportTest
{
    /**
     * Params for transport
     */
    private Map<String, String> params;

    /**
     * Setup
     *
     * @throws Exception Anything goes wrong
     */
    @Before
    public void setUp() throws Exception
    {
        params = new HashMap<String, String>();
    }

    @Test
    public void testGetUriWithoutParams()
    {
        Transport transport = new JmsTransport();

        assertEquals("JMS", transport.getType());

        // --- Try to get the URI
        try
        {
            transport.toUriString();
            fail("Transport should not work without required parameters");
        }
        catch (Exception e)
        {
            // --- That's ok
        }
    }

    @Test
    public void testGetSimplestUri()
    {
        // --- Try to get this URI: activemq:foo.bar?brokerURL=tcp://localhost:61616
        params.put(JmsTransport.PARAM_DESTINATION_NAME, "foo.bar");
        params.put(JmsTransport.PARAM_BROKER_URL, "tcp://localhost:61616");
        Transport transport = new JmsTransport(new BaseConfigurable(params));

        try
        {
            String uri = transport.toUriString();

            assertTrue("Returned uri isn't as expected: " + uri,
                uri.indexOf("activemq:foo.bar?brokerURL=tcp://localhost:61616") > -1);
        }
        catch (Exception e)
        {
            fail("Shouldn't be getting an exception here: " + e.getMessage());
        }
    }

    @Test
    public void testGetUriForActiveMQTopic()
    {
        // --- Try to get this URI: activemq:topic:foo.bar?brokerURL=tcp://localhost:61616
        params.put(JmsTransport.PARAM_ACTIVE_MQ, "true");
        params.put(JmsTransport.PARAM_DESTINATION_NAME, "foo.bar");
        params.put(JmsTransport.PARAM_IS_TOPIC, "true");
        params.put(JmsTransport.PARAM_BROKER_URL, "tcp://localhost:61616");
        Transport transport = new JmsTransport(new BaseConfigurable(params));
        try
        {
            String uri = transport.toUriString();

            assertTrue("Returned uri isn't as expected: " + uri,
                uri.indexOf("activemq:topic:foo.bar?brokerURL=tcp://localhost:61616") > -1);
        }
        catch (Exception e)
        {
            fail("Shouldn't be getting an exception here: " + e.getMessage());
        }
    }
}
