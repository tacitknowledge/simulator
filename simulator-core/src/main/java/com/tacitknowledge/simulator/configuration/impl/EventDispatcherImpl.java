package com.tacitknowledge.simulator.configuration.impl;

import com.tacitknowledge.simulator.configuration.EventDispatcher;
import com.tacitknowledge.simulator.configuration.SimulatorEventListener;

import java.util.List;
import java.util.ArrayList;

/**
 * Default Implementation class for EventDispatcher Interface
 *
 * @author Daniel Valencia (dvalencia@tacitknowledge.com)
 * @author Raul Huerta (rhuerta@tacitknowledge.com)
 */
public class EventDispatcherImpl implements EventDispatcher {

    /**
     * Default Constructor
     */
    public EventDispatcherImpl() {
        this.eventListeners = new ArrayList<SimulatorEventListener>();
    }

    /**
     * Contains all registered event listeners
     */
    private List<SimulatorEventListener> eventListeners;

    /**
     * {@inheritDoc}
     */
    public void addSimulatorEventListener(SimulatorEventListener listener) {
        this.eventListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    public List<SimulatorEventListener> getSimulatorEventListeners() {
        return this.eventListeners;
    }
}
