package com.tacitknowledge.simulator.camel;

import java.util.Map;

import org.apache.camel.builder.RouteBuilder;
import org.junit.Test;

import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.SimulatorCamelTestSupportBase;
import com.tacitknowledge.simulator.Transport;
import com.tacitknowledge.simulator.formats.PlainTextAdapter;
import com.tacitknowledge.simulator.impl.ConversationFactory;

/**
 * Test class for RouteManager
 *
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 * @author Alexandru Dereveanco (adereveanco@tacitknowledge.com)
 */
public class RouteManagerTest extends SimulatorCamelTestSupportBase
{
    /**
     * Timeout for the camel end point to wait
     */
    private static final int TIMEOUT = 500;


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

        public void setParameters(Map<String, String> parameters)
        {

        }

        /**
         * @param bound      Configurable bound
         * @param parameters Configurable parameter values
         */
        public void setBoundAndParameters(int bound, Map<String, String> parameters)
        {

        }

        /**
         * @return The bounding (IN or OUT) of the configurable instance
         */
        public int getBound()
        {
            return 0;
        }
    };

    /**
     * A transport to use in tests
     */
    private final Transport inTransport1 = new Transport()
    {

        public String getType()
        {
            return "Mock In Transport1";
        }

        public String toUriString()
        {
            return "direct:start1";
        }

        public void setParameters(Map<String, String> parameters)
        {

        }

        /**
         * @param bound      Configurable bound
         * @param parameters Configurable parameter values
         */
        public void setBoundAndParameters(int bound, Map<String, String> parameters)
        {

        }

        /**
         * @return The bounding (IN or OUT) of the configurable instance
         */
        public int getBound()
        {
            return 0;
        }
    };


    /**
     * Conversation to be used in tests
     */
    private final Conversation conversation1 = ConversationFactory.createConversation("conversation1", 
                                                                                      inTransport, 
                                                                                      outTransport, 
                                                                                      new PlainTextAdapter(), 
                                                                                      new PlainTextAdapter());

    /**
     * Conversation to be used in tests
     */
    private final Conversation conversation2 = ConversationFactory.createConversation("conversation2", 
                                                                                      inTransport1,
                                                                                      outTransport1, 
                                                                                      new PlainTextAdapter(), 
                                                                                      new PlainTextAdapter());
    {
        conversation1.addScenario("javascript", "true", "text");
        conversation1.addScenario("javascript", "true", "text");
    }

    /**
     * Test for activating a route.
     *
     * @throws Exception in case of an error.
     */
    @Test
    public void testActivate() throws Exception
    {
        routeManager.activate(conversation1);

        sendMessage();

        resultEndpoint.assertIsSatisfied();
    }

    /**
     * Test for two calls to activate the same route.
     * Expecting only one route in camel.
     *
     * @throws Exception in case of an error.
     */
    @Test
    public void testTwoCallsToActivateWithTheSameConversation() throws Exception
    {
        routeManager.activate(conversation1);

        routeManager.activate(conversation1);

        sendMessage();

        resultEndpoint.assertIsSatisfied();

        assertCollectionSize(routeManager.getContext().getRoutes(), 1);
    }

    /**
     * Testing bouncing the same route
     *
     * @throws Exception in case of an error.
     */
    @Test
    public void testActivateDeactivateActivateAgain() throws Exception
    {
        routeManager.activate(conversation1);

        routeManager.deactivate(conversation1);

        resultEndpoint.setResultWaitTime(TIMEOUT);

        sendMessage();

        resultEndpoint.assertIsNotSatisfied();

        routeManager.activate(conversation1);

        resultEndpoint.setResultWaitTime(TIMEOUT);

        sendMessage();

        resultEndpoint.assertIsSatisfied();
    }

    /**
     * Testing activating two different conversations.
     * Expecting two routes in camel.
     *
     * @throws Exception in case of an error.
     */
    @Test
    public void testTwoCallsToActivateWithDifferentConversations() throws Exception
    {
        routeManager.activate(conversation1);

        routeManager.activate(conversation2);

        assertCollectionSize(routeManager.getRouteCollection().getRoutes(), 2);
    }

    /**
     * Testing the deactivation of a route.
     *
     * @throws Exception in case of an error.
     */
    @Test
    public void testDeactivate() throws Exception
    {
        routeManager.activate(conversation1);

        routeManager.deactivate(conversation1);

        resultEndpoint.setResultWaitTime(TIMEOUT);

        sendMessage();

        resultEndpoint.assertIsNotSatisfied();
    }

    /**
     * Overriding the route builder as suggested by Camel testing
     * techniques.
     *
     * @return a route builder.
     * @throws Exception in case of an error.
     */
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception
    {
        routeManager = new RouteManagerImpl();
        return routeManager;
    }

    /**
     * Utility method to send a message to Camel.
     *
     * @throws InterruptedException in case of an error
     */
    private void sendMessage() throws InterruptedException
    {
        String expectedBody = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><matched/>";

        resultEndpoint.expectedBodiesReceived(expectedBody);

        template.sendBody(expectedBody);
    }
}
