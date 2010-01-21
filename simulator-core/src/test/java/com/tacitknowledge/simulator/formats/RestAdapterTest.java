package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorPojo;
import junit.framework.TestCase;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.DefaultMessage;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Enumeration;

import static junit.framework.Assert.assertNull;

/**
 * Test class for RestAdapterTest
 *
 * @author Raul Huerta (rhuerta@tacitknowledge.com)
 */
public class RestAdapterTest {

    private RestAdapter adapter;

    @Before
    public void setUp()
    {
        adapter = (RestAdapter) AdapterFactory.getInstance().getAdapter(FormatConstants.REST);
    }

    @Test
    public void testCreateSimulatorPojo() throws FormatAdapterException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(RestAdapter.PARAM_EXTRACTION_PATTERN, "/system/:system_id/conversation/:conv_id");

        adapter.setParameters(params);

        //Mocking HTTP Request
        HttpServletRequest request = mock(HttpServletRequest.class);
        stub(request.getRequestURI()).toReturn("/system/1/conversation/23");
        stub(request.getMethod()).toReturn("GET");
        Enumeration enumer = mock(Enumeration.class);
        stub(enumer.hasMoreElements()).toReturn(false);
        stub(request.getParameterNames()).toReturn(enumer);

        CamelContext context = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(context);
        Message message = new DefaultMessage();
        message.setBody(request);
        exchange.setIn(message);

        SimulatorPojo pojo = adapter.createSimulatorPojo(exchange);

        Map<String, Object> map = pojo.getRoot();

        Map<String, Object> obj = (Map<String, Object>) map.get("obj");

        Map<String, Object> requestMap = (Map<String, Object>) obj.get("request");

        Map<String, Object> responseMap = (Map<String, Object>) obj.get("response");

        Map<String, Object> requestParams = (Map<String, Object>) requestMap.get("params");

        //Testing values extracted from the url
        assertEquals("1", requestParams.get("system_id"));
        assertEquals("23", requestParams.get("conv_id"));
        assertEquals("GET", requestMap.get("method"));

        //Testing the default values for content type and status code
        assertEquals("text/html", responseMap.get("contentType"));
        assertEquals("200", responseMap.get("statusCode"));


        //Test now with a url that comes with format
        stub(request.getRequestURI()).toReturn("/system/99/conversation/2435.xml");
        stub(request.getMethod()).toReturn("POST");
        exchange = new DefaultExchange(context);
        message = new DefaultMessage();
        message.setBody(request);
        exchange.setIn(message);

        pojo = adapter.createSimulatorPojo(exchange);

        map = pojo.getRoot();

        obj = (Map<String, Object>) map.get("obj");

        requestMap = (Map<String, Object>) obj.get("request");

        requestParams = (Map<String, Object>) requestMap.get("params");

        assertEquals("99", requestParams.get("system_id"));
        assertEquals("2435", requestParams.get("conv_id"));
        assertEquals("POST", requestMap.get("method"));

    }

}