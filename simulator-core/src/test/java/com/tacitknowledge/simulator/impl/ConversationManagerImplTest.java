package com.tacitknowledge.simulator.impl;

import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.ConversationManager;
import com.tacitknowledge.simulator.ConversationNotFoundException;
import com.tacitknowledge.simulator.ConversationScenario;
import com.tacitknowledge.simulator.RouteManager;
import com.tacitknowledge.simulator.SimulatorCamelTestSupportBase;
import com.tacitknowledge.simulator.SimulatorException;
import com.tacitknowledge.simulator.configuration.SimulatorEventListener;
import com.tacitknowledge.simulator.configuration.EventDispatcher;
import com.tacitknowledge.simulator.camel.RouteManagerImpl;
import com.tacitknowledge.simulator.formats.AdapterFactory;
import com.tacitknowledge.simulator.formats.FormatConstants;
import org.junit.Test;
import org.junit.Ignore;
import org.apache.camel.Exchange;

import java.util.List;

/**
 * Test class for ConversationManagerImpl
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class ConversationManagerImplTest extends SimulatorCamelTestSupportBase
{

    @Test
    public void testGetCsvFormatParameters() throws ConfigurableException
    {
        RouteManager routeManager = new RouteManagerImpl();
        ConversationManager manager = new ConversationManagerImpl(routeManager);

        List<List> params = manager.getAdapterParameters(FormatConstants.CSV);
        assertEquals(4, params.size());
    }

    @Test
    public void testCreateConversation() throws SimulatorException
    {
        RouteManager routeManager = new RouteManagerImpl();
        ConversationManager manager = new ConversationManagerImpl(routeManager);

        Conversation conversation = manager.createOrUpdateConversation(null,
            "testCreateConversation",
            inTransport,
            outTransport,
            AdapterFactory.getInstance().getAdapter(FormatConstants.JSON),
            AdapterFactory.getInstance().getAdapter(FormatConstants.JSON), "");
        assertNotNull(conversation);
        assertNotNull(conversation.getInboundTransport());
        assertNotNull(conversation.getOutboundTransport());
    }

    @Test
    public void testCreateConversationWithWrongFormat()
    {
        RouteManager routeManager = new RouteManagerImpl();
        ConversationManager manager = new ConversationManagerImpl(routeManager);

        Conversation conversation = null;
        try
        {
            conversation = manager.createOrUpdateConversation(null,
                "testCreateConversationWithWrongFormat",
                inTransport,
                outTransport,
                AdapterFactory.getInstance().getAdapter("WTF?"),
                AdapterFactory.getInstance().getAdapter("WTF?"), "");
            fail();
        }
        catch (SimulatorException e)
        {
            //everything is ok.
        }
        assertNull(conversation);

    }

    @Test
    public void testIsActiveConversationNotFound() throws SimulatorException
    {
        ConversationManager manager = new ConversationManagerImpl();
        assertFalse(manager.isActive(1234));
    }


    @Test
    public void testIsActive() throws SimulatorException, ConversationNotFoundException
    {
        RouteManager routeManager = new RouteManagerImpl();
        ConversationManager manager = new ConversationManagerImpl(routeManager);

        Conversation conversation = null;
        conversation = manager.createOrUpdateConversation(1,
            "testIsActive",
            inTransport,
            outTransport,
            AdapterFactory.getInstance().getAdapter(FormatConstants.PLAIN_TEXT),
            AdapterFactory.getInstance().getAdapter(FormatConstants.PLAIN_TEXT),
            "");
        assertFalse(manager.isActive(conversation.getId()));
        manager.activate(conversation.getId());
        assertTrue(manager.isActive(conversation.getId()));
    }


    @Test
    public void testCreateOrUpdateScenarioConversationDoesntExits()
        throws SimulatorException, ConversationNotFoundException
    {
        RouteManager routeManager = new RouteManagerImpl();
        ConversationManager manager = new ConversationManagerImpl(routeManager);

        Conversation conversation = manager.createOrUpdateConversation(1,
            "testCreateOrUpdateScenarioConversationDoesntExits",
            inTransport,
            outTransport,
            AdapterFactory.getInstance().getAdapter(FormatConstants.PLAIN_TEXT),
            AdapterFactory.getInstance().getAdapter(FormatConstants.PLAIN_TEXT), "");
        ConversationScenario scenario = manager.createOrUpdateConversationScenario(2, 2, "javascript", "true", "2+2");
        assertNull(scenario);
    }


    @Test
    public void testCreateScenario() throws SimulatorException, ConversationNotFoundException
    {
        RouteManager routeManager = new RouteManagerImpl();
        ConversationManager manager = new ConversationManagerImpl(routeManager);

        Conversation conversation = manager.createOrUpdateConversation(1, "testCreateScenario", inTransport, outTransport,
                AdapterFactory.getInstance().getAdapter(FormatConstants.PLAIN_TEXT),
                AdapterFactory.getInstance().getAdapter(FormatConstants.PLAIN_TEXT),
                "defaultScenario");
        ConversationScenario scenario = manager.createOrUpdateConversationScenario(1, 2, "javascript", "true", "2+2");
        assertNotNull(scenario);
        assertEquals("javascript", scenario.getScriptLanguage());
        assertEquals("true", scenario.getCriteriaScript());
        assertEquals("defaultScenario\n2+2", scenario.getTransformationScript());


        ConversationScenario scenario1 = manager.createOrUpdateConversationScenario(1, 2, "ruby", "ttttrue", "2+2+2");
        assertSame(scenario, scenario1);

        assertEquals("ruby", scenario.getScriptLanguage());
        assertEquals("ttttrue", scenario.getCriteriaScript());
        assertEquals("defaultScenario\n2+2+2", scenario.getTransformationScript());

    }

    @Test
    public void testDeleteConversation() throws SimulatorException, ConversationNotFoundException
    {
        RouteManager routeManager = new RouteManagerImpl();
        ConversationManager manager = new ConversationManagerImpl(routeManager);

        Conversation conversation = manager.createOrUpdateConversation(1, "testDeleteConversation", inTransport, outTransport,
                AdapterFactory.getInstance().getAdapter(FormatConstants.PLAIN_TEXT),
                AdapterFactory.getInstance().getAdapter(FormatConstants.PLAIN_TEXT),
                "");
        ConversationScenario scenario = manager.createOrUpdateConversationScenario(1, 2, "javascript", "true", "2+2");
        assertNotNull(scenario);

        manager.activate(1);
        manager.deleteConversation(1);
        assertTrue(!manager.isActive(1));
    }

    @Test
    public void testDelete() throws SimulatorException, ConversationNotFoundException
    {
        RouteManager routeManager = new RouteManagerImpl();
        ConversationManager manager = new ConversationManagerImpl(routeManager);
        manager.deleteConversation(1234);
    }

    @Test
    public void testConversationExists() throws SimulatorException, ConversationNotFoundException
    {
        RouteManager routeManager = new RouteManagerImpl();
        ConversationManager manager = new ConversationManagerImpl(routeManager);
        assertFalse(manager.conversationExists(1));
        Conversation conversation = manager.createOrUpdateConversation(1, "testConversationExists",
                inTransport, outTransport,
                AdapterFactory.getInstance().getAdapter(FormatConstants.PLAIN_TEXT),
                AdapterFactory.getInstance().getAdapter(FormatConstants.PLAIN_TEXT),
                "");
        assertTrue(manager.conversationExists(1));
        manager.activate(1);
        assertTrue(manager.conversationExists(1));
        manager.deactivate(1);
        assertTrue(manager.conversationExists(1));
    }

    @Test
    public void testDefaultScenarioWasExecuted() throws SimulatorException, ConversationNotFoundException, InterruptedException
    {
        ConversationManager manager = new ConversationManagerImpl(routeManager);

        Conversation conversation = manager.createOrUpdateConversation(1, "testDefaultScenarioWasExecuted",
                inTransport, outTransport,
                AdapterFactory.getInstance().getAdapter(FormatConstants.PLAIN_TEXT),
                AdapterFactory.getInstance().getAdapter(FormatConstants.PLAIN_TEXT),
                "var testVar=123");
        ConversationScenario scenario = manager.createOrUpdateConversationScenario(1, 2, "javascript", "true",
            "testVar=testVar+1\n" +
                "testVar");

        manager.activate(1);

        sendMessage("124.0");

        manager.deactivate(1);
        resultEndpoint.assertIsSatisfied();
        assertNotNull(scenario);
    }

    public static final String TEST_IMPL_1 =
            "com.tacitknowledge.simulator.impl.ConversationManagerImplTest$TestEventListenerImpl1";
    public static final String TEST_IMPL_2 =
            "com.tacitknowledge.simulator.impl.ConversationManagerImplTest$TestEventListenerImpl2";
    public static final String TEST_IMPL_3 =
            "com.tacitknowledge.simulator.impl.ConversationManagerImplTest$TestEventListenerImpl3";

    
    @Test
    public void testRegisterListeners() {
        ConversationManager manager = new ConversationManagerImpl(routeManager);
        manager.registerListeners(System.getProperty("user.dir") + "/src/test/resources/listeners");
        List<SimulatorEventListener> listeners = EventDispatcher.getInstance().getSimulatorEventListeners();
        assertTrue(listeners.size() > 0);
        //need to do this since equals method could be different among implementations
        boolean impl1Found = false;
        boolean impl2Found = false;
        boolean impl3Found = false;
        for(SimulatorEventListener listener : listeners) {
            String className = listener.getClass().getName();
            if(className.equals(TEST_IMPL_1)) {
                impl1Found = true;
            } else if(className.equals(TEST_IMPL_2)) {
                impl2Found = true;
            } else if(className.equals(TEST_IMPL_3)) {
                impl3Found = true;
            }
        }
        assertTrue(impl1Found && impl2Found && impl3Found);
    }

    /**
     * SimulatorEventListener implementation for testing purposes
     */
    public static final class TestEventListenerImpl1 implements SimulatorEventListener {

        public TestEventListenerImpl1(){}


        public void onNewMessage(Exchange exchange, Conversation conversation) {
        }

        public void onMatchingScenario(Exchange exchange, Conversation conversation) {
        }

        public void onResponseBuilt(Exchange exchange, Conversation conversation) {
        }

        public void onResponseSent(Exchange exchange, Conversation conversation) {
        }
    }

    /**
     * SimulatorEventListener implementation for testing purposes
     */
    public static final class TestEventListenerImpl2 implements SimulatorEventListener {


        public void onNewMessage(Exchange exchange, Conversation conversation) {
        }

        public void onMatchingScenario(Exchange exchange, Conversation conversation) {
        }

        public void onResponseBuilt(Exchange exchange, Conversation conversation) {
        }

        public void onResponseSent(Exchange exchange, Conversation conversation) {
        }
    }

    /**
     * SimulatorEventListener implementation for testing purposes
     */
    public static final class TestEventListenerImpl3 implements SimulatorEventListener {


        public void onNewMessage(Exchange exchange, Conversation conversation) {
        }

        public void onMatchingScenario(Exchange exchange, Conversation conversation) {
        }

        public void onResponseBuilt(Exchange exchange, Conversation conversation) {
        }

        public void onResponseSent(Exchange exchange, Conversation conversation) {
        }
    }
}
