package com.tacitknowledge.simulator.camel;

import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.Transport;
import com.tacitknowledge.simulator.formats.JsonAdapter;
import com.tacitknowledge.simulator.impl.ConversationImpl;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Test class for RouteManager
 *
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 * @author Alexandru Dereveanco (adereveanco@tacitknowledge.com)
 */
public class RouteManagerTest extends CamelTestSupport
{
    /** Timeout for the camel end point to wait */
    private static final int TIMEOUT = 500;

    /** Class under test */
    private RouteManagerImpl routeManager = new RouteManagerImpl();

    /** MockEnd point to receive the message */
    @EndpointInject(uri = "mock:result")
    private MockEndpoint resultEndpoint;

    /** MockEntry point to send the message */
    @Produce(uri = "direct:start")
    private ProducerTemplate template;

    /**
     * A transport to use in tests
     */
    private final Transport inTransport = new Transport()
    {

        public String getType()
        {
            return "file";
        }

        public String toUriString()
        {
            return "direct:start";
        }

        public List<List> getParametersList()
        {
            return null;
        }

        public void setParameters(Map<String, String> parameters)
        {

        }
    };

    /**
     * A transport to use in tests
     */
    private final Transport outTransport = new Transport()
    {

        public String getType()
        {
            return "file";
        }

        public String toUriString()
        {
            return "mock:result";
        }

        public List<List> getParametersList()
        {
            return null;
        }

        public void setParameters(Map<String, String> parameters)
        {

        }
    };

    /**
     * A transport to use in tests
     */
    private final Transport outTransport1 = new Transport()
    {

        public String getType()
        {
            return "file";
        }

        public String toUriString()
        {
            return "mock:result1";
        }

        public List<List> getParametersList()
        {
            return null;
        }

        public void setParameters(Map<String, String> parameters)
        {

        }
    };

    /** Conversation to be used in tests */
    private final Conversation conversation1
        = new ConversationImpl(1, inTransport, outTransport, new JsonAdapter(), new JsonAdapter());

    /** Conversation to be used in tests */
    private final Conversation conversation2
        = new ConversationImpl(1, inTransport, outTransport1, new JsonAdapter(), new JsonAdapter());

    /**
     * Test for activating a route.
     * @throws Exception in case of an error.
     */
    @Test
    public void testActivate() throws Exception
    {
        sendMessage();

        resultEndpoint.assertIsSatisfied();
    }

    /**
     * Test for two calls to activate the same route.
     * Expecting only one route in camel.
     * @throws Exception in case of an error.
     */
    @Test
    public void testTwoCallsToActivateWithTheSameConversation() throws Exception
    {
        routeManager.activate(conversation1);

        sendMessage();

        resultEndpoint.assertIsSatisfied();

        assertCollectionSize(routeManager.getContext().getRouteDefinitions(), 1);
    }

    /**
     * Testing bouncing the same route
     * @throws Exception in case of an error.
     */
    @Test
    public void testActivateDeactivateActivateAgain() throws Exception
    {
        routeManager.deactivate(conversation1);

        resultEndpoint.setResultWaitTime(TIMEOUT);

        sendMessage();

        resultEndpoint.assertIsNotSatisfied();

        routeManager.activate(conversation1);

        sendMessage();

        resultEndpoint.assertIsSatisfied();
    }

    /**
     * Testing activating two different conversations.
     * Expecting two routes in camel.
     * @throws Exception in case of an error.
     */
    @Test
    public void testTwoCallsToActivateWithDifferentConversations() throws Exception
    {
        routeManager.activate(conversation2);

        assertCollectionSize(routeManager.getRouteCollection().getRoutes(), 2);
    }

    /**
     * Testing the deactivation of a route.
     * @throws Exception in case of an error.
     */
    @Test
    public void testDeactivate() throws Exception
    {
        routeManager.deactivate(conversation1);

        resultEndpoint.setResultWaitTime(TIMEOUT);

        sendMessage();

        resultEndpoint.assertIsNotSatisfied();
    }

    /**
     * Overriding the route builder as suggested by Camel testing
     * techniques.
     * @throws Exception in case of an error.
     * @return a route builder.
     */
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception
    {
        routeManager.activate(conversation1);
        return routeManager;
    }

    /**
     * Utility method to send a message to Camel.
     * @throws InterruptedException in case of an error
     */
    private void sendMessage() throws InterruptedException
    {
        String expectedBody = "<matched/>";

        resultEndpoint.expectedBodiesReceived(expectedBody);

        template.sendBody(expectedBody);
    }
}
