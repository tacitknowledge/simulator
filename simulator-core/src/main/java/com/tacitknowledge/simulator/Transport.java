package com.tacitknowledge.simulator;

import java.util.List;
import java.util.Map;

/**
 * Wrapper for transport configuration. Each transport implementation should hold the required
 * attributes for each transport type (server, port, queue, path, etc)
 * Parameter name constants must start with PARAM_ for convention.
 * GUI implementations should set proper parameter values according to parameter types.
 * Boolean parameters are always optional. Each implementation should set its default value.
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public interface Transport
{
    /**
     * Returns a List of parameters the implementing instance uses.
     * Each list element is itself a List to describe the parameter as follows:
     * - 0 : Parameter name
     * - 1 : Parameter description. Useful for GUI rendition
     * - 2 : Parameter type (string, date, boolean). Useful for GUI rendition.
     * - 3 : Required or Optional parameter. Useful for GUI validation.
     *
     * @return List of Parameters for the implementing Transport.
     */
    List<List> getParametersList();

    /**
     * Allows to set Transport parameters.
     * Each implementing Transport must define its parameters.
     *
     * @param parameters The Transport parameters
     */
    void setParameters(Map<String, String> parameters);

    /**
     * Returns the Transport type
     *
     * @return transport type
     */
    String getType();

    /**
     * Returns a valid String URI representation of this transport for Camel route creation e.g.:
     * file://path/to/file/directory , jms:queue/myqueue ,
     *
     * @return URI representation of the transport
     * @throws TransportException If a required parameter is missing or not properly formatted.
     */
    String toUriString() throws TransportException;
}
