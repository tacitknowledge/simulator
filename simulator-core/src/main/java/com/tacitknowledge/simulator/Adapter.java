package com.tacitknowledge.simulator;

import org.apache.camel.Exchange;

import java.util.Map;


/**
 * Adapter interface for different data formats.
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public interface Adapter
{
    /**
     * Adapts the data from the inbound transport format to the SimulatorPojo object graph.
     * Used in the simulation.
     *
     * @param exchange The Camel exchange
     * @return a SimulatorPojo object constructed based on the inboud transport data.
     * @throws FormatAdapterException in case the incoming data object is not in correct format
     * @throws ConfigurableException  If missing required parameters
     * @see com.tacitknowledge.simulator.formats.BaseAdapter
     */
    Map<String, Object> adaptForInput(Exchange exchange)
        throws ConfigurableException, FormatAdapterException;

    /**
     * Adapts the data from the simulation result SimulatorPojo into the desired format used
     * for outbound transport.
     *
     * @param scriptExecutionResult The resulting object obtained from the execution script
     * @param exchange  Camel Exchange object
     * @return generic data object.
     * @throws FormatAdapterException If the pojo object is not properly structured
     *                                or an error occurs during processing.
     * @throws ConfigurableException If any required parameter is missing
     */
    Object adaptToOutput(Object scriptExecutionResult, Exchange exchange)
        throws ConfigurableException, FormatAdapterException;

    /**
     * Validate that all the required parameters have been provided.
     *
     * @throws ConfigurableException If any required parameter has not been set.
     */
    void validateParameters() throws ConfigurableException;

}
