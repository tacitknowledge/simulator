package com.tacitknowledge.simulator.impl;

import java.util.List;

import org.apache.camel.Exchange;
import org.junit.Test;

import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.ConversationManager;
import com.tacitknowledge.simulator.ConversationNotFoundException;
import com.tacitknowledge.simulator.ConversationScenario;
import com.tacitknowledge.simulator.RouteManager;
import com.tacitknowledge.simulator.SimulatorCamelTestSupportBase;
import com.tacitknowledge.simulator.SimulatorException;
import com.tacitknowledge.simulator.camel.RouteManagerImpl;
import com.tacitknowledge.simulator.configuration.EventDispatcher;
import com.tacitknowledge.simulator.configuration.SimulatorEventListener;
import com.tacitknowledge.simulator.formats.JsonAdapter;
import com.tacitknowledge.simulator.formats.PlainTextAdapter;

/**
 * Test class for ConversationManagerImpl
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class ConversationManagerImplTest extends SimulatorCamelTestSupportBase
{
    @Test
    public void testCreateConversation() throws SimulatorException
    {
        RouteManager routeManager = new RouteManagerImpl();
        ConversationManager manager = new ConversationManagerImpl(routeManager);

        Conversation conversation = manager.createOrUpdateConversation(
                "testCreateConversation",
                inTransport,
                outTransport,
                new JsonAdapter(),
                new JsonAdapter());
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
            conversation = manager.createOrUpdateConversation("testCreateConversationWithWrongFormat",
                                                              inTransport,
                                                              outTransport,
                                                              null,
                                                              new JsonAdapter());
            fail();
        }
        catch (IllegalArgumentException e)
        {
            //everything is ok.
        }
        
        assertNull(conversation);
    }

    @Test
    public void testIsActiveConversationNotFound() throws SimulatorException
    {
        ConversationManager manager = new ConversationManagerImpl();
        assertFalse(manager.isActive("1234"));
    }


    @Test
    public void testIsActive() throws SimulatorException, ConversationNotFoundException
    {
        RouteManager routeManager = new RouteManagerImpl();
        ConversationManager manager = new ConversationManagerImpl(routeManager);

        Conversation conversation = null;
        conversation = manager.createOrUpdateConversation(
                "testIsActive",
                inTransport,
                outTransport,
                new PlainTextAdapter(),
                new PlainTextAdapter());
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

        manager.createOrUpdateConversation("first",
                                           inTransport,
                                           outTransport,
                                           new PlainTextAdapter(),
                                           new PlainTextAdapter());
        ConversationScenario scenario = manager.createOrUpdateConversationScenario("second", 
                                                                                    2, 
                                                                                    "javascript", 
                                                                                    "true", 
                                                                                    "2+2");
        assertNull(scenario);
    }


    @Test
    public void testCreateScenario() throws SimulatorException, ConversationNotFoundException
    {
        RouteManager routeManager = new RouteManagerImpl();
        ConversationManager manager = new ConversationManagerImpl(routeManager);

        manager.createOrUpdateConversation("testCreateScenario", 
                                           inTransport, 
                                           outTransport,
                                           new PlainTextAdapter(),
                                           new PlainTextAdapter());
        ConversationScenario scenario = manager.createOrUpdateConversationScenario("testCreateScenario", 2, "javascript", "true", "2+2");
        assertNotNull(scenario);
        assertEquals("javascript", scenario.getScriptLanguage());
        assertEquals("true", scenario.getCriteriaScript());
        assertEquals("2+2", scenario.getTransformationScript());
    }

    @Test
    public void testDeleteConversation() throws SimulatorException, ConversationNotFoundException
    {
        RouteManager routeManager = new RouteManagerImpl();
        ConversationManager manager = new ConversationManagerImpl(routeManager);

        manager.createOrUpdateConversation("testDeleteConversation", 
                                           inTransport, 
                                           outTransport,
                                           new PlainTextAdapter(),
                                           new PlainTextAdapter());
        ConversationScenario scenario = manager.createOrUpdateConversationScenario("testDeleteConversation", 2, "javascript", "true", "2+2");
        assertNotNull(scenario);

        manager.activate("testDeleteConversation");
        manager.deleteConversation("testDeleteConversation");
        assertFalse(manager.isActive("testDeleteConversation"));
    }

    @Test
    public void testDelete() throws SimulatorException, ConversationNotFoundException
    {
        RouteManager routeManager = new RouteManagerImpl();
        ConversationManager manager = new ConversationManagerImpl(routeManager);
        manager.deleteConversation("1234");
    }

    @Test
    public void testConversationExists() throws SimulatorException, ConversationNotFoundException
    {
        RouteManager routeManager = new RouteManagerImpl();
        ConversationManager manager = new ConversationManagerImpl(routeManager);
        assertFalse(manager.conversationExists("testConversationExists"));
        manager.createOrUpdateConversation("testConversationExists",
                                           inTransport, 
                                           outTransport,
                                           new PlainTextAdapter(),
                                           new PlainTextAdapter());
        assertTrue(manager.conversationExists("testConversationExists"));
        manager.activate("testConversationExists");
        assertTrue(manager.conversationExists("testConversationExists"));
        manager.deactivate("testConversationExists");
        assertTrue(manager.conversationExists("testConversationExists"));
    }

    public static final String TEST_IMPL_1 =
            "com.tacitknowledge.simulator.impl.ConversationManagerImplTest$TestEventListenerImpl1";
    public static final String TEST_IMPL_2 =
            "com.tacitknowledge.simulator.impl.ConversationManagerImplTest$TestEventListenerImpl2";
    public static final String TEST_IMPL_3 =
            "com.tacitknowledge.simulator.impl.ConversationManagerImplTest$TestEventListenerImpl3";


    @Test
    public void testRegisterListeners()
    {
        ConversationManager manager = new ConversationManagerImpl(routeManager);
        manager.registerListeners(System.getProperty("user.dir") + "/src/test/resources/listeners");
        List<SimulatorEventListener> listeners = EventDispatcher.getInstance().getSimulatorEventListeners();
        assertTrue(listeners.size() > 0);
        //need to do this since equals method could be different among implementations
        boolean impl1Found = false;
        boolean impl2Found = false;
        boolean impl3Found = false;
        
        for (SimulatorEventListener listener : listeners)
        {
            String className = listener.getClass().getName();
            if (className.equals(TEST_IMPL_1))
            {
                impl1Found = true;
            }
            else if (className.equals(TEST_IMPL_2))
            {
                impl2Found = true;
            }
            else if (className.equals(TEST_IMPL_3))
            {
                impl3Found = true;
            }
        }

        assertTrue(impl1Found && impl2Found && impl3Found);
    }

    /**
     * SimulatorEventListener implementation for testing purposes
     */
    public static final class TestEventListenerImpl1 implements SimulatorEventListener
    {

        public TestEventListenerImpl1()
        {
        }


        public void onNewMessage(Exchange exchange, Conversation conversation)
        {
        }

        public void onMatchingScenario(Exchange exchange, Conversation conversation)
        {
        }

        public void onResponseBuilt(Exchange exchange, Conversation conversation)
        {
        }

        public void onResponseSent(Exchange exchange, Conversation conversation)
        {
        }
    }

    /**
     * SimulatorEventListener implementation for testing purposes
     */
    public static final class TestEventListenerImpl2 implements SimulatorEventListener
    {


        public void onNewMessage(Exchange exchange, Conversation conversation)
        {
        }

        public void onMatchingScenario(Exchange exchange, Conversation conversation)
        {
        }

        public void onResponseBuilt(Exchange exchange, Conversation conversation)
        {
        }

        public void onResponseSent(Exchange exchange, Conversation conversation)
        {
        }
    }

    /**
     * SimulatorEventListener implementation for testing purposes
     */
    public static final class TestEventListenerImpl3 implements SimulatorEventListener
    {


        public void onNewMessage(Exchange exchange, Conversation conversation)
        {
        }

        public void onMatchingScenario(Exchange exchange, Conversation conversation)
        {
        }

        public void onResponseBuilt(Exchange exchange, Conversation conversation)
        {
        }

        public void onResponseSent(Exchange exchange, Conversation conversation)
        {
        }
    }
}
