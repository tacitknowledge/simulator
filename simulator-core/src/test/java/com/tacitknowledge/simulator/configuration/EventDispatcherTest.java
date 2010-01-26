package com.tacitknowledge.simulator.configuration;

import com.tacitknowledge.simulator.Conversation;

import java.util.List;

import org.apache.camel.Exchange;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Test class for EventDispatcher
 *
 * @author Daniel Valencia (dvalencia@tacitknowledge.com)
 * @author Raul Huerta (rhuerta@tacitknowledge.com)
 */
public class EventDispatcherTest {

    private EventDispatcher eventDispatcher;

    @Before
    public void setUp() throws Exception {
        eventDispatcher = EventDispatcher.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        eventDispatcher.removeAllSimulatorEventListeners();
    }

    /**
     * Tests that calling getInstance always returns a not null object
     */
    @Test
    public void testGetInstance() {
        assertNotNull(eventDispatcher);
    }

    /**
     * Test adding an event listener
     */
    @Test
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
    @Test
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
