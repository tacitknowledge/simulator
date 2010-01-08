package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.Transport;
import com.tacitknowledge.simulator.TransportException;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * Test class for SoapTransport
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class SoapTransportTest extends TestCase
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
    @Override
    protected void setUp() throws Exception
    {
        params = new HashMap<String, String>();
    }

    public void testGetUriWithoutParams()
    {
        Transport transport = new SoapTransport();
        assertEquals("SOAP", transport.getType());

        try
        {
            transport.toUriString();
            fail("Transport should not work without required parameters");
        }
        catch (TransportException e)
        {
            // --- That's ok
        }
    }

    public void testGetSimplestUri()
    {
        // --- Try to get this URI: cxf://http://localhost:9002/helloworld
        params.put(SoapTransport.PARAM_SERVICE_URL, "http://localhost:9002/helloworld");
        Transport transport = new SoapTransport(params);

        try
        {
            String uri = transport.toUriString();

            assertTrue("Returned uri isn't as expected: " + uri,
                uri.indexOf("cxf://http://localhost:9002/helloworld") > -1);
        }
        catch (TransportException e)
        {
            fail("Shouldn't be getting an exception here: " + e.getMessage());
        }
    }

    public void testGetUriWillAllParameters()
    {
        // --- Try to get this URI: cxf://http://localhost:9002/helloworld?serviceClass=org.apache.camel.Hello&
        //      wsdlURL=wsdl/hello.wsdl&serviceName={http:?//org.apache.camel}ServiceName&
        //      portName={http:?//org.apache.camel}PortName
        params.put(SoapTransport.PARAM_SERVICE_URL, "http://localhost:9002/helloworld");
        params.put(SoapTransport.PARAM_SERVICE_CLASS, "org.apache.camel.Hello");
        params.put(SoapTransport.PARAM_WSDL_URL, "wsdl/hello.wsdl");
        params.put(SoapTransport.PARAM_SERVICE_NAME, "{http:?//org.apache.camel}ServiceName");
        params.put(SoapTransport.PARAM_PORT_NAME, "{http:?//org.apache.camel}PortName");
        Transport transport = new SoapTransport(params);

        try
        {
            String uri = transport.toUriString();

            String expectedUri =
                    "cxf://http://localhost:9002/helloworld?" +
                        "serviceClass=org.apache.camel.Hello&" +
                        "wsdlURL=wsdl/hello.wsdl&" +
                        "serviceName={http:?//org.apache.camel}ServiceName&" +
                        "portName={http:?//org.apache.camel}PortName";

            assertTrue("Returned uri isn't as expected: " + uri, uri.indexOf(expectedUri) > -1);
        }
        catch (TransportException e)
        {
            fail("Shouldn't be getting an exception here: " + e.getMessage());
        }
    }
}
