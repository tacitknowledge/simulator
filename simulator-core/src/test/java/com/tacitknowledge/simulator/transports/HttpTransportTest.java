package com.tacitknowledge.simulator.transports;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import com.tacitknowledge.simulator.BaseConfigurable;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Before;
import org.junit.Test;

import com.tacitknowledge.simulator.Configurable;
import com.tacitknowledge.simulator.Transport;

/**
 * @author galo
 */
public class HttpTransportTest
{
    private Transport transport;
    private Map<String, String> params;

    RouteBuilder builder;

    @Before
    public void setUp()
    {
        transport = new RestTransport();
        params = new HashMap<String, String>();

        builder = new RouteBuilder()
        {
            public void configure()
            {
            }
        };
    }

    @Test
    public void testGetUriWithoutParams()
    {
        assertEquals(TransportConstants.REST, transport.getType());

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
    public void testGetSimplestInUri()
    {
        // --- Try to get this URI: jetty:http://localhost/mytestapp/myservices
        params.put(HttpTransport.PARAM_RESOURCE_URI, "/mytestapp/myservices");
        transport = new HttpTransport(new BaseConfigurable(Configurable.BOUND_IN,params));

        try
        {
            String uri = transport.toUriString();

            assertTrue("Returned uri isn't as expected: " + uri,
                uri.indexOf("jetty:http://0.0.0.0/mytestapp/myservices") > -1);
        }
        catch (Exception e)
        {
            fail("Shouldn't be getting an exception here: " + e.getMessage());
        }
    }

    @Test
    public void testGetFullInUri()
    {
        // --- Try to get this URI: jetty:http://localhost:8080/mytestapp/myservices
        params.put(HttpTransport.PARAM_PORT, "8080");
        params.put(HttpTransport.PARAM_RESOURCE_URI, "/mytestapp/myservices");
        transport = new HttpTransport(new BaseConfigurable(params));

        try
        {
            String uri = transport.toUriString();

            assertTrue("Returned uri isn't as expected: " + uri,
                uri.indexOf("jetty:http://0.0.0.0:8080/mytestapp/myservices") > -1);
        }
        catch (Exception e)
        {
            fail("Shouldn't be getting an exception here: " + e.getMessage());
        }
    }
    
    @Test
    public void testGetOutUri()
    {
        // --- Try to get this URI: direct:end
        params.put(HttpTransport.PARAM_HTTP_OUT, "true");
        transport = new HttpTransport(new BaseConfigurable(Configurable.BOUND_OUT,params));

        try
        {
            String uri = transport.toUriString();

            assertEquals("Returned uri isn't as expected: " + uri,
                HttpTransport.MOCK_RESULT, uri);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Shouldn't be getting an exception here: " + e.getMessage());
        }
    }

    @Test
    public void testHttpInRoute()
    {
        params.put(HttpTransport.PARAM_PORT, "9696");
        params.put(HttpTransport.PARAM_RESOURCE_URI, "/mytestapp");
        transport = new HttpTransport(new BaseConfigurable(Configurable.BOUND_IN,params));

        Transport outTransport = new MockOutTransport();
        Map<String, String> pars = new HashMap<String, String>();
        pars.put(HttpTransport.PARAM_HTTP_OUT, "true");
        transport = new HttpTransport(new BaseConfigurable(Configurable.BOUND_IN,params));

        try
        {
            CamelContext context = new DefaultCamelContext();
            context.start();

            System.out.println(transport.toUriString());

            RouteDefinition def = builder.from(transport.toUriString());
            def.bean(new Processor(){
                public void process(Exchange exchange)
                {
                    exchange.getOut().setBody("test");
                }
            });
            
            def.to(outTransport.toUriString());

            context.addRoutes(builder);

            // --- Now send the HTTP request
            HttpClient client = new HttpClient();
            PostMethod method = new PostMethod("http://0.0.0.0:9696/mytestapp");


            method.addParameter("system_id", "1");
            method.addParameter("system_name", "My Test System");
            method.addParameter("system_description", "My test system for HTTP camel route");
            method.addParameter("system_language", "javascript");


            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK)
            {
                fail("Failed with status code " + statusCode + ": " + method.getStatusLine());
            }

            // Read the response body.
            byte[] responseBody = method.getResponseBody();
            
            assertEquals("test", new String(responseBody));

            context.stop();
            
        } catch(Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }
}
