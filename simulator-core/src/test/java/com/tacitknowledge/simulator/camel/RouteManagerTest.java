package com.tacitknowledge.simulator.camel;

import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.Transport;
import com.tacitknowledge.simulator.formats.CsvAdapter;
import com.tacitknowledge.simulator.formats.JsonAdapter;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Test class for RouteManager
 *
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 * @author Alexandru Dereveanco (adereveanco@tacitknowledge.com)
 */
public class RouteManagerTest extends CamelTestSupport
{
    /** Class under test */
    RouteManager routeManager = new RouteManager();

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;

    final private Transport inTransport = new Transport()
    {

        public String getType()
        {
            return "file";
        }

        public String toUriString()
        {
            return "direct:start";
        }
    };
    
    final private Transport outTransport = new Transport()
    {

        public String getType()
        {
            return "yyyy";
        }

        public String toUriString()
        {
            return "mock:result";
        }
    };
    
    final private Transport outTransport1 = new Transport()
    {

        public String getType()
        {
            return "yyyy";
        }

        public String toUriString()
        {
            return "mock:result1";
        }
    };
    
    final Conversation conversation1 = new Conversation(1, inTransport, outTransport, new JsonAdapter(), new JsonAdapter());
    final Conversation conversation2 = new Conversation(1, inTransport, outTransport1, new JsonAdapter(), new JsonAdapter());
    
    @Test
    public void testActivate() throws Exception
    {
        sendMessage();
        
        resultEndpoint.assertIsSatisfied();
    }
    
    @Test
    public void testTwoCallsToActivateWithTheSameConversation() throws Exception
    {
        routeManager.activate(conversation1);
        
        sendMessage();
        
        resultEndpoint.assertIsSatisfied();
        
        assertCollectionSize(routeManager.getContext().getRouteDefinitions(), 1);
    }
    
    @Test
    public void testTwoCallsToActivateWithDifferentConversations() throws Exception
    {
        routeManager.activate(conversation2);
        
        assertCollectionSize(routeManager.getRouteCollection().getRoutes(), 2);
    }
    
    @Test
    public void testDeactivate() throws Exception
    {
        routeManager.deactivate(conversation1);
        
        resultEndpoint.setResultWaitTime(500);
        
        sendMessage();

        resultEndpoint.assertIsNotSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception
    {
        routeManager.activate(conversation1);
        return routeManager;
    }
    
    private void sendMessage() throws InterruptedException
    {
        String expectedBody = "<matched/>";

        resultEndpoint.expectedBodiesReceived(expectedBody);

        template.sendBody(expectedBody);
    }
}
