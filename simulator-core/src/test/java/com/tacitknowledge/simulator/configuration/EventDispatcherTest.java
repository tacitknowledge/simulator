package com.tacitknowledge.simulator.configuration;

import com.tacitknowledge.simulator.Conversation;
import junit.framework.TestCase;

import java.util.List;

import org.apache.camel.Exchange;

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
        eventDispatcher = EventDispatcher.getInstance();
    }

    @Override
     protected void tearDown() throws Exception {
        eventDispatcher.removeAllSimulatorEventListeners();
    }

    /**
     * Tests that calling getInstance always returns a not null object
     */
    public void testGetInstance() {
        assertNotNull(eventDispatcher);
    }

    /**
     * Test adding an event listener
     */
    public void testAddEventListener() {
        List<SimulatorEventListener> eventListeners = eventDispatcher.getSimulatorEventListeners();
        assertTrue(eventListeners.size() == 0);
        SimulatorEventListener listener = new SimulatorEventListener() {


            public void onNewMessage(Exchange exchange, Conversation conversation) {

            }

            public void onMatchingScenario(Exchange exchange, Conversation conversation) {

            }

            public void onResponseBuilt(Exchange exchange, Conversation conversation) {

            }

            public void onResponseSent(Exchange exchange, Conversation conversation) {

            }
        };
        eventDispatcher.addSimulatorEventListener(listener);
        assertTrue(eventListeners.size() == 1);
    }

    /**
     * Test dispatch event method
     */
    public void testDispatchEvent(){

        final SampleClass sample = new SampleClass();

        assertEquals("", sample.getName());

        eventDispatcher.addSimulatorEventListener(new SimulatorEventListener(){

            public void onNewMessage(Exchange exchange, Conversation conversation) {
                sample.setName("Modified");
            }

            public void onMatchingScenario(Exchange exchange, Conversation conversation) {

            }

            public void onResponseBuilt(Exchange exchange, Conversation conversation) {

            }

            public void onResponseSent(Exchange exchange, Conversation conversation) {
               
            }
        });

        eventDispatcher.dispatchEvent(SimulatorEventType.NEW_MESSAGE, null, null);

        assertEquals("Modified",sample.getName());
    }

    /**
     * Inner class for testing purposes only
     */
    private static class SampleClass{
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        private String name = "";

    }
    
}
