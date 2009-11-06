package com.tacitknowledge.simulator;

import com.tacitknowledge.simulator.formats.FormatAdapterException;

/**
 * Adapter interface for different data formats.
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 * @param <E> Generic data object
 */
public interface Adapter<E>
{
    /**
     * Adapts the data from the inbound transport to the SimulatorPojo object graph.
     * Used in the simulation.
     * @param e Generic information object
     * @return a SimulatorPojo object
     * @throws FormatAdapterException in case the incoming data object is not in correct format
     */
    SimulatorPojo adaptFrom(E e) throws FormatAdapterException;

    /**
     * Adapts the data from the simulation result SimulatorPojo into a generic object used
     * for outbound transport.
     * @param pojo SimulatorPojo object
     * @return generic data object.
     */
    E adaptTo(SimulatorPojo pojo);
}
