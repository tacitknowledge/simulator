package com.tacitknowledge.simulator;

/**
 * Adapter interface for different formats of data.
 * 
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public interface Adapter<E>
{
    /**
     * Adapts the data from the inbound transport to the SimulatorPojo object graph.
     * Used in the simulation.
     * @param e Generic information object
     * @return a SimulatorPojo object
     */
    SimulatorPojo adaptFrom(E e);

    /**
     * Adapts the data from the simulation result SimulatorPojo into a generic object used
     * for outbound transport.
     * @param pojo SimulatorPojo object
     * @return generic data object.
     */
    E adaptTo(SimulatorPojo pojo);
}
