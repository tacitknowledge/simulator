package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.StructuredSimulatorPojo;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.DefaultMessage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import static com.tacitknowledge.simulator.formats.PostCodeAnywhereAdapter.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** @author Adrian Neaga (aneaga@tacitknowledge.com) */
public class PostCodeAnywhereAdapterTest
{
    /** The Address request param name */
    private static final String ADDRESS = "Address";

    /** The Key request param name */
    private static final String KEY = "Key";

    /** The Postcode request param name */
    private static final String POST_CODE = "Postcode";


    /** Adapter being tested */
    private PostCodeAnywhereAdapter postCodeAdapter;

    @Before
    public void setup()
    {
        postCodeAdapter = new PostCodeAnywhereAdapter();
    }


    @Test
    public void testCreatesPojoWithAddressAndKeyPopulated() throws Exception
    {
        HttpServletRequest request = mock(HttpServletRequest.class);

        String key = "WG39-YN16-GH19-RY96";
        String address = "TW9+1EP";

        Hashtable<String, String> parameters = new Hashtable<String, String>();

        parameters.put(KEY, key);
        parameters.put(ADDRESS, address);

        when(request.getParameterNames()).thenReturn(parameters.keys());
        when(request.getParameter(KEY)).thenReturn(key);
        when(request.getParameter(ADDRESS)).thenReturn(address);

        Exchange exchange = createExchangeMessageForRequest(request);

        SimulatorPojo pojo = postCodeAdapter.createSimulatorPojo(exchange);

        Map<String, String> postCodeData =
            (Map<String, String>) pojo.getRoot().get(POST_CODE_REQUEST);

        Assert.assertNotNull(postCodeData);
        Assert.assertEquals(address, postCodeData.get(ADDRESS));
        Assert.assertEquals(key, postCodeData.get(KEY));
        Assert.assertNull(postCodeData.get(POST_CODE));
    }


    @Test
    public void testCreatesPojoWithPostcodeAndKeyPopulated() throws Exception
    {
        HttpServletRequest request = mock(HttpServletRequest.class);

        String key = "WG39-YN16-GH19-RY96";
        String postCode = "TW9+1EP";

        Hashtable<String, String> parameters = new Hashtable<String, String>();

        parameters.put(KEY, key);
        parameters.put(POST_CODE, postCode);

        when(request.getParameterNames()).thenReturn(parameters.keys());
        when(request.getParameter(KEY)).thenReturn(key);
        when(request.getParameter(POST_CODE)).thenReturn(postCode);

        Exchange exchange = createExchangeMessageForRequest(request);

        SimulatorPojo pojo = postCodeAdapter.createSimulatorPojo(exchange);


        Map<String, String> postCodeData =
            (Map<String, String>) pojo.getRoot().get(POST_CODE_REQUEST);

        Assert.assertNotNull(postCodeData);
        Assert.assertEquals(postCode, postCodeData.get(POST_CODE));
        Assert.assertEquals(key, postCodeData.get(KEY));
        Assert.assertNull(postCodeData.get(ADDRESS));
    }

    @Test
    public void testPojoStringRepresentation() throws Exception {
        String body = "body";

        SimulatorPojo pojo = new StructuredSimulatorPojo();

        Map<String, String> postCodeData = new HashMap<String, String>();
        pojo.getRoot().put(POST_CODE_REQUEST, postCodeData);

        postCodeData.put(PostCodeAnywhereAdapter.RESPONSE, body);

        Assert.assertEquals(body, postCodeAdapter.getString(pojo, null));
    }



    private Exchange createExchangeMessageForRequest(HttpServletRequest request)
    {
        CamelContext context = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(context);
        Message message = new DefaultMessage();
        message.setBody(request);
        exchange.setIn(message);
        return exchange;
    }
}
