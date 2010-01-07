package com.tacitknowledge.simulator.configuration;

import junit.framework.TestCase;

import java.util.List;

import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.configuration.impl.EventDispatcherImpl;

/**
 * Test class for EventDispatcher
 *
 * @author Daniel Valencia (dvalencia@tacitknowledge.com)
 * @author Raul Huerta (rhuerta@tacitknowledge.com)
 */
public class EventDispatcherTest extends TestCase {

    private EventDispatcher eventDispatcher;

    @Override
    protected void setUp() throws Exception {
        eventDispatcher = new EventDispatcherImpl();
    }

    /**
     * Test adding an event listener
     */
    public void testAddEventListener() {
        List<SimulatorEventListener> eventListeners = eventDispatcher.getSimulatorEventListeners();
        assertTrue(eventListeners.size() == 0);
        SimulatorEventListener listener = new SimulatorEventListener() {

            public void onNewMessage(String messageBody, Conversation conversation) {}

            public void onMatchingScenario(String messageBody, Conversation conversation) {}

            public void onResponseBuilt(String messageBody, Conversation conversation) {}

            public void onResponseSent(String messageBody, Conversation conversation) {}
        };
        eventDispatcher.addSimulatorEventListener(listener);
        assertTrue(eventListeners.size() == 1);
    }
    
}
