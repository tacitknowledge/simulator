package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.RouteManager;
import com.tacitknowledge.simulator.Transport;
import com.tacitknowledge.simulator.camel.RouteManagerImpl;
import com.tacitknowledge.simulator.formats.RestAdapter;
import com.tacitknowledge.simulator.impl.ConversationImpl;
import org.apache.camel.CamelContext;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniel Valencia (mailto:dvalencia@tacitknowledge.com)
 */
public class RestTransportIntegrationTest {
    CamelContext context;
    Transport inTransport;
    Transport outTransport;
    Adapter inAdapter;
    Adapter outAdapter;
    RouteManager routeManager;

    @Before
    public void setup(){
        inTransport = TransportFactory.getInstance().getTransport(TransportConstants.REST);
        outTransport = TransportFactory.getInstance().getTransport(TransportConstants.REST);
        inAdapter = new RestAdapter();
        outAdapter = new RestAdapter();
        routeManager = new RouteManagerImpl();
    }

    @Test
    public void succesfulRestTest() throws Exception{

        Map<String, String> transportParams = new HashMap<String, String>();
        transportParams.put(HttpTransport.PARAM_RESOURCE_URI, "/collection");
        transportParams.put(HttpTransport.PARAM_PORT, "9001");
        inTransport.setParameters(transportParams);


        Map<String, String> adapterParams = new HashMap<String, String>();
        adapterParams.put(RestAdapter.PARAM_EXTRACTION_PATTERN, "/collection/:id" );
        adapterParams.put(RestAdapter.PARAM_OBJECT_NAME, "obj1");
        inAdapter.setParameters(adapterParams);

        Map<String, String> pars = new HashMap<String, String>();
        pars.put(HttpTransport.PARAM_HTTP_OUT, "true");
        outTransport.setParameters(pars);

        Conversation conv = new ConversationImpl(1, "Rest conversation", inTransport, outTransport, inAdapter, outAdapter, "");
        String criteriaScript       = "obj1.request.params['id']=='89'";
        String transformationScript = "obj1.response.body='<html><body>ID=89</body></html>';" +
                                      "obj1.response.statusCode='201';" +
                                      "obj1;";
        conv.addOrUpdateScenario(1,"javascript", criteriaScript, transformationScript);

        String criteriaScript2       = "obj1.request.params.id=='90'";
        String transformationScript2 = "obj1.response.body='<html><body>ID=90</body></html>';" +
                                       "obj1.response.statusCode='200';" +
                                       "obj1;";
        conv.addOrUpdateScenario(2,"javascript", criteriaScript2, transformationScript2);



        //Activate the route
        routeManager.activate(conv);

        HttpClient client = new HttpClient();
        GetMethod requestMethod = new GetMethod("http://0.0.0.0:9001/collection/89");


        int statusCode = client.executeMethod(requestMethod);

        if (statusCode == HttpStatus.SC_CREATED)
        {
            byte[] responseBody = requestMethod.getResponseBody();
            assertEquals("<html><body>ID=89</body></html>", new String(responseBody));

        }else{
            fail("Should be a 201");
        }

        requestMethod = new GetMethod("http://0.0.0.0:9001/collection/90");
        statusCode = client.executeMethod(requestMethod);

        if (statusCode == HttpStatus.SC_OK)
        {
            byte[] responseBody = requestMethod.getResponseBody();
            assertEquals("<html><body>ID=90</body></html>", new String(responseBody));

        }else{
            fail("Should be an OK");
        }

        routeManager.deactivate(conv);
    }
}
