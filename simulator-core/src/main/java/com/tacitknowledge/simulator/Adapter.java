package com.tacitknowledge.simulator;

import java.util.List;
import java.util.Map;


/**
 * Adapter interface for different data formats.
 * Each adapter implementation should hold the required parameters for each adapter format.
 * Parameter name constants must start with PARAM_ for convention.
 * GUI implementations should set proper parameter values according to parameter types.
 * Boolean parameters are always optional. Each implementation should set its default value.
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 * @param <E> Generic data object
 */
public interface Adapter<E>
{
    /**
     * Returns the List of parameters the implementing instance uses.
     * Each list element is itself a List to describe the parameter as follows:
     * - 0 : Parameter name
     * - 1 : Parameter description. Useful for GUI rendition
     * - 2 : Parameter type. Useful for GUI rendition.
     * - 3 : Required or Optional parameter. Useful for GUI validation.
     * @return List of Paramaters for the implementing Adapter.
     */
    List<List> getParametersList();

    /**
     * Allows to set Adapter parameters.
     * Each implementing Adapter must defined its parameters.
     * @param parameters The Adapter parameters
     */
    void setParameters(Map<String, String> parameters);

    /**
     * Adapts the data from the inbound transport format to the SimulatorPojo object graph.
     * Used in the simulation.
     * @param e Generic information object
     * @return a SimulatorPojo object constructed based on the inboud transport data.
     * @throws FormatAdapterException in case the incoming data object is not in correct format
     *      or missing required parameters. Also @see #BaseAdapter
     */
    SimulatorPojo adaptFrom(E e) throws FormatAdapterException;

    /**
     * Adapts the data from the simulation result SimulatorPojo into the desired format used
     * for outbound transport.
     * @param pojo SimulatorPojo object
     * @return generic data object.
     * @throws FormatAdapterException If the pojo object is not properly structured
     *      , an error occurs during convertion.
     */
    E adaptTo(SimulatorPojo pojo) throws FormatAdapterException;
}
