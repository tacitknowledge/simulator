package com.tacitknowledge.simulator;

import java.util.Map;

/**
 * Each configurable implementation should hold its necessary
 * attributes (server, port, queue, path, etc).
 * Parameter name constants must start with PARAM_ for convention.
 * GUI implementations should set proper parameter values according to parameter types.
 * Boolean parameters are always optional. Each implementation should set its default value.
 *
 * @author galo
 */
public interface Configurable
{
    /**
     * Constant for defining an inbound configurable
     */
    int BOUND_IN = 0;

    /**
     * Constant for defining an outbound configurable
     */
    int BOUND_OUT = 1;

    /**
     *
     * @param parameters Configurable parameter values
     */
    void setParameters(Map<String, String> parameters);

    /**
     *
     * @param bound Configurable bound
     * @param parameters Configurable parameter values
     */
    void setBoundAndParameters(int bound, Map<String, String> parameters);

    /**
     * @return The bounding (IN or OUT) of the configurable instance
     */
    int getBound();

    /**
     * @param name The parameter name. Parameter names should be defined by each implementation.
     * @return The parameter value or null if not defined.
     */
    String getParamValue(final String name);

    void setParamValue(final String name, final String value);

    /**
     *
     * @return The provided parameters
     */
    Map<String, String> getParameters();

}
