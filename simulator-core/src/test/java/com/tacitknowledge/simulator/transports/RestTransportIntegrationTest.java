package com.tacitknowledge.simulator.transports;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import com.tacitknowledge.simulator.*;
import org.apache.camel.CamelContext;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.tacitknowledge.simulator.camel.RouteManagerImpl;
import com.tacitknowledge.simulator.formats.RestAdapter;
import com.tacitknowledge.simulator.impl.ConversationFactory;
import com.tacitknowledge.simulator.impl.ScenarioFactory;

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
    public void setup() throws Exception {
        inTransport = new RestTransport();
        outTransport = new MockOutTransport();
        inAdapter = new RestAdapter();
        outAdapter = new RestAdapter();
        routeManager = new RouteManagerImpl();
        routeManager.start();
    }

//    @Ignore
    @Test
    public void succesfulRestTest() throws Exception{

        Map<String, String> transportParams = new HashMap<String, String>();
        transportParams.put(HttpTransport.PARAM_RESOURCE_URI, "/collection");
        transportParams.put(HttpTransport.PARAM_PORT, "9001");
        inTransport.setParameters(transportParams);


        BaseConfigurable configurable = new BaseConfigurable();
        Map<String, String> adapterParams = new HashMap<String, String>();
        adapterParams.put(RestAdapter.PARAM_EXTRACTION_PATTERN, "/collection/:id" );
        adapterParams.put(RestAdapter.PARAM_OBJECT_NAME, "obj1");
        configurable.setParameters(adapterParams);
        inAdapter = new RestAdapter(configurable);

        Map<String, String> pars = new HashMap<String, String>();
        pars.put(HttpTransport.PARAM_HTTP_OUT, "true");
        outTransport.setParameters(pars);

        ScenarioFactory scenarioFactory = new ScenarioFactory();
        ConversationFactory conversationFactory = new ConversationFactory();
        Conversation conv = conversationFactory.createConversation("Rest conversation", inTransport, outTransport, inAdapter, outAdapter);
        String criteriaScript       = "obj1.request.params['id']=='89'";
        String transformationScript = "obj1.response.body='<html><body>ID=89</body></html>';" +
                                      "obj1.response.statusCode='201';" +
                                      "obj1;";
        
        Scenario scenario = scenarioFactory.createScenario("file1.scn", "javascript", criteriaScript, transformationScript);
        conv.addScenario(scenario);

        String criteriaScript2       = "obj1.request.params.id=='90'";
        String transformationScript2 = "obj1.response.body='<html><body>ID=90</body></html>';" +
                                       "obj1.response.statusCode='200';" +
                                       "obj1;";
        
        scenario = scenarioFactory.createScenario("file2.scn", "javascript", criteriaScript2, transformationScript2);
        conv.addScenario(scenario);



        //Activate the route
        routeManager.activate(conv);

        HttpClient client = new HttpClient();
        GetMethod requestMethod = new GetMethod("http://0.0.0.0:9001/collection/89");


        int statusCode = client.executeMethod(requestMethod);

        assertEquals("201",HttpStatus.SC_CREATED,statusCode);
        if (statusCode == HttpStatus.SC_CREATED)
        {
            byte[] responseBody = requestMethod.getResponseBody();
            assertEquals("<html><body>ID=89</body></html>", new String(responseBody));

        }else{
            fail("Should be a 201");
        }

        requestMethod = new GetMethod("http://0.0.0.0:9001/collection/90");
        statusCode = client.executeMethod(requestMethod);
        assertEquals("200",HttpStatus.SC_OK,statusCode);
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
