package com.tacitknowledge.simulator.configuration;

import com.tacitknowledge.simulator.Conversation;
import org.apache.camel.Exchange;

/**
 * SimulatorEventListener interface
 * This interface can be used to register a listener for
 * some events that are triggered during the routing process.
 *
 * @author Daniel Valencia (dvalencia@tacitknowledge.com)
 * @author Raul Huerta (rhuerta@tacitknowledge.com)
 */
public interface SimulatorEventListener
{

    /**
     * Triggered when a new message arrives.
     * @param exchange - String representing a message
     * @param conversation - Conversation object
     */
    void onNewMessage(Exchange exchange, Conversation conversation);

    /**
     * Triggered when an existing scenario matches with the input criteria.
     * @param exchange - String representing a message
     * @param conversation - Conversation object
     */
    void onMatchingScenario(Exchange exchange, Conversation conversation);

    /**
     * Triggered when the output result is constructed
     * @param exchange - String representing a message
     * @param conversation - Conversation object
     */
    void onResponseBuilt(Exchange exchange, Conversation conversation);

    /**
     * Triggered after the response has been sent.
     * @param exchange - String representing a message
     * @param conversation - Conversation object
     */
    void onResponseSent(Exchange exchange, Conversation conversation);
}
