package com.tacitknowledge.simulator.impl;

import java.util.List;

import org.apache.camel.Exchange;
import org.junit.Test;

import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.ConversationManager;
import com.tacitknowledge.simulator.SimulatorCamelTestSupportBase;
import com.tacitknowledge.simulator.configuration.EventDispatcher;
import com.tacitknowledge.simulator.configuration.SimulatorEventListener;

/**
 * Test class for ConversationManagerImpl
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class ConversationManagerImplTest extends SimulatorCamelTestSupportBase
{
    public static final String TEST_IMPL_1 = "com.tacitknowledge.simulator.impl.ConversationManagerImplTest$TestEventListenerImpl1";

    public static final String TEST_IMPL_2 = "com.tacitknowledge.simulator.impl.ConversationManagerImplTest$TestEventListenerImpl2";

    public static final String TEST_IMPL_3 = "com.tacitknowledge.simulator.impl.ConversationManagerImplTest$TestEventListenerImpl3";

    @Test
    public void testRegisterListeners()
    {
        ConversationManager manager = new ConversationManagerImpl(null, null);
        manager.registerListeners(System.getProperty("user.dir") + "/src/test/resources/listeners");
        List<SimulatorEventListener> listeners = EventDispatcher.getInstance()
                .getSimulatorEventListeners();
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
        {}

        public void onNewMessage(Exchange exchange, Conversation conversation)
        {}

        public void onMatchingScenario(Exchange exchange, Conversation conversation)
        {}

        public void onResponseBuilt(Exchange exchange, Conversation conversation)
        {}

        public void onResponseSent(Exchange exchange, Conversation conversation)
        {}
    }

    /**
     * SimulatorEventListener implementation for testing purposes
     */
    public static final class TestEventListenerImpl2 implements SimulatorEventListener
    {

        public void onNewMessage(Exchange exchange, Conversation conversation)
        {}

        public void onMatchingScenario(Exchange exchange, Conversation conversation)
        {}

        public void onResponseBuilt(Exchange exchange, Conversation conversation)
        {}

        public void onResponseSent(Exchange exchange, Conversation conversation)
        {}
    }

    /**
     * SimulatorEventListener implementation for testing purposes
     */
    public static final class TestEventListenerImpl3 implements SimulatorEventListener
    {

        public void onNewMessage(Exchange exchange, Conversation conversation)
        {}

        public void onMatchingScenario(Exchange exchange, Conversation conversation)
        {}

        public void onResponseBuilt(Exchange exchange, Conversation conversation)
        {}

        public void onResponseSent(Exchange exchange, Conversation conversation)
        {}
    }
}
