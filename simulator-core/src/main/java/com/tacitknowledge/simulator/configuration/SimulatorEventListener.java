package com.tacitknowledge.simulator.configuration;

import com.tacitknowledge.simulator.Conversation;

/**
 * SimulatorEventListener interface
 * This interface can be used register a listener for
 * some events that are triggered during the routing process.
 *
 * @author Daniel Valencia (dvalencia@tacitknowledge.com)
 * @author Raul Huerta (rhuerta@tacitknowledge.com)
 */
public interface SimulatorEventListener {

    /**
     * Triggered when a new message arrives.
     * @param messageBody - String representing a message
     * @param conversation - Conversation object
     */
    void onNewMessage(String messageBody, Conversation conversation);

    /**
     * Triggered when an existing scenario matches with the input criteria.
     * @param messageBody - String representing a message
     * @param conversation - Conversation object
     */
    void onMatchingScenario(String messageBody, Conversation conversation);

    /**
     * Triggered when the output result is constructed
     * @param messageBody - String representing a message
     * @param conversation - Conversation object
     */
    void onResponseBuilt(String messageBody, Conversation conversation);

    /**
     * Triggered after the response has been sent.
     * @param messageBody - String representing a message
     * @param conversation - Conversation object
     */
    void onResponseSent(String messageBody, Conversation conversation);
}
