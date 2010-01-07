package com.tacitknowledge.simulator.configuration;

/**
 * Enumeration to hold the available event types that simulator throws.
 *
 * @author Daniel Valencia (dvalencia@tacitknowledge.com)
 * @author Raul Huerta (rhuerta@tacitknowledge.com)
 */
public enum SimulatorEventType {
    /**
     * New message event
     */
    NEW_MESSAGE,
    /**
     * Scenario matched event
     */
    SCENARIO_MATCHED,
    /**
     * Response built event
     */
    RESPONSE_BUILT,
    /**
     * Response sent event
     */
    RESPONSE_SENT
}
