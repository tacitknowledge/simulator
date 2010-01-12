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

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Test class for RestAdapterTest
 *
 * @author Raul Huerta (rhuerta@tacitknowledge.com)
 */
public class RestAdapterTest extends TestCase {

    private RestAdapter adapter;

    public void setUp()
    {
        adapter = (RestAdapter) AdapterFactory.getAdapter(FormatConstants.REST);
    }

    public void testCreateSimulatorPojo() throws FormatAdapterException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(RestAdapter.PARAM_RESOURCE, "system");

        adapter.setParameters(params);

        //Mocking HTTP Request
        HttpServletRequest request = mock(HttpServletRequest.class);
        stub(request.getRequestURI()).toReturn("/system/1/conversation/2");
        stub(request.getMethod()).toReturn("GET");
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put(RestAdapter.PARAM_HTTP_METHOD, "GET");
        parameterMap.put(RestAdapter.PARAM_RESOURCE, "system");
        parameterMap.put("system_name", "my_system_name");
        parameterMap.put("system_description", "my_system_description");
        stub(request.getParameterMap()).toReturn(parameterMap);

        CamelContext context = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(context);
        Message message = new DefaultMessage();
        message.setBody(request);
        exchange.setIn(message);

        SimulatorPojo pojo = adapter.createSimulatorPojo(exchange);

        Map<String, Object> map = pojo.getRoot();

        assertNotNull(map.get(RestAdapter.PARAM_RESOURCE));
        assertEquals(parameterMap.get(RestAdapter.PARAM_RESOURCE), map.get(RestAdapter.PARAM_RESOURCE));
        assertNotNull(map.get(RestAdapter.PARAM_HTTP_METHOD));
        assertEquals(parameterMap.get(RestAdapter.PARAM_HTTP_METHOD), map.get(RestAdapter.PARAM_HTTP_METHOD));
        assertNotNull(map.get("system_name"));
        assertEquals(parameterMap.get("system_name"), map.get("system_name"));
        assertNotNull(map.get("system_description"));
        assertEquals(parameterMap.get("system_description"), map.get("system_description"));


        String getString = adapter.getString(pojo);
        assertNotNull(getString);
        String originalString = parameterMap.get(RestAdapter.PARAM_HTTP_METHOD) + " - " + parameterMap.get(RestAdapter.PARAM_RESOURCE) +
                "?system_name=" + parameterMap.get("system_name") + "&system_description=" + parameterMap.get("system_description"); 
        assertEquals(originalString, getString);

    }
   
}