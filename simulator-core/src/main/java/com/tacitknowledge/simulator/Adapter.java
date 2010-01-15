package com.tacitknowledge.simulator;

import org.apache.camel.Exchange;

import java.util.List;
import java.util.Map;


/**
 * Adapter interface for different data formats.
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 * @param <E> Generic data object
 */
public interface Adapter<E> extends Configurable
{
    /**
     * Adapts the data from the inbound transport format to the SimulatorPojo object graph.
     * Used in the simulation.
     *
     * @param exchange The Camel exchange
     * @return a SimulatorPojo object constructed based on the inboud transport data.
     * @throws FormatAdapterException in case the incoming data object is not in correct format
     *                                or missing required parameters. Also @see #BaseAdapter
     */
    Map<String, Object> generateBeans(Exchange exchange)
            throws ConfigurableException, FormatAdapterException;

    /**
     * Adapts the data from the simulation result SimulatorPojo into the desired format used
     * for outbound transport.
     *
     * @param scriptExecutionResult
     * @param exchange
     * @return generic data object.
     * @throws FormatAdapterException If the pojo object is not properly structured
     *                                , an error occurs during convertion.
     */
    Object adaptTo(Object scriptExecutionResult, Exchange exchange)
            throws ConfigurableException, FormatAdapterException;
}
