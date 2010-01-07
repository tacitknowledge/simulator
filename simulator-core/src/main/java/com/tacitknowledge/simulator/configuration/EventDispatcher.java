package com.tacitknowledge.simulator.configuration;

import java.util.List;

/**
 * EventDispatcher class
 *
 * @author Daniel Valencia (dvalencia@tacitknowledge.com)
 * @author Raul Huerta (rhuerta@tacitknowledge.com)
 */

public interface EventDispatcher {

    /**
     * Add an event listener to a dispatcher
     * @param listener - SimulatorEventListener implementation to be added
     */
    void addSimulatorEventListener(SimulatorEventListener listener);

    /**
     * Returns all SimulatorEventListener objects added
     * @return - List of SimulatorEventListener objects
     */
    List<SimulatorEventListener> getSimulatorEventListeners();
}
