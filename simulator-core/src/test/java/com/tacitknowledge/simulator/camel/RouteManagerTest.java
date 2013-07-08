package com.tacitknowledge.simulator.camel;

import java.util.Map;

import org.junit.Test;

import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.Scenario;
import com.tacitknowledge.simulator.SimulatorCamelTestSupportBase;
import com.tacitknowledge.simulator.Transport;
import com.tacitknowledge.simulator.TransportException;
import com.tacitknowledge.simulator.formats.PlainTextAdapter;
import com.tacitknowledge.simulator.impl.ConversationFactory;
import com.tacitknowledge.simulator.impl.ScenarioFactory;

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

    private ScenarioFactory scenarioFactory = new ScenarioFactory();
    
    private ConversationFactory conversationFactory = new ConversationFactory(); 
    
    /**
     * A transport to use in tests
     */
    private final Transport outTransport1 = new MockTransport("mock", "mock:result1");

    /**
     * A transport to use in tests
     */
    private final Transport inTransport1 = new MockTransport("mock", "direct:start1");
    
    /**
     * Conversation to be used in tests
     */
    private final Conversation conversation1 = conversationFactory.createConversation(
            "conversation1", inTransport, outTransport, new PlainTextAdapter(),
            new PlainTextAdapter());

    /**
     * Conversation to be used in tests
     */
    private final Conversation conversation2 = conversationFactory.createConversation(
            "conversation2", inTransport1, outTransport1, new PlainTextAdapter(),
            new PlainTextAdapter());
    {
        Scenario scenario = scenarioFactory.createScenario("file.scn", "javascript", "true", "text");
        conversation1.addScenario(scenario);
        conversation1.addScenario(scenario);
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
        sendMessage("<matched/>");
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
        sendMessage("<matched/>");
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
        routeManager.activate(conversation1);
        resultEndpoint.setResultWaitTime(TIMEOUT);
        sendMessage("<matched/>");
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
        assertCollectionSize(routeManager.getContext().getRoutes(), 0);
    }

    private static class MockTransport implements Transport
    {
        private String type;
        private String uri;
        
        public MockTransport(String type, String uri)
        {
            this.type = type;
            this.uri = uri;
        }
        
        public void setParameters(Map<String, String> parameters)
        {}

        public void setBoundAndParameters(int bound, Map<String, String> parameters)
        {}

        public int getBound()
        {
            return 0;
        }

        public String getType()
        {
            return type;
        }

        public String toUriString() throws ConfigurableException, TransportException
        {
            return uri;
        }

        public String getParamValue(String name) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public void setParamValue(String name, String value) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public Map<String, String> getParameters() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public void validateParameters() throws ConfigurableException {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
