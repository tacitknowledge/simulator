package com.tacitknowledge.simulator;

import java.util.List;
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
    public static final int BOUND_IN = 0;

    /**
     * Constant for defining an outbound configurable
     */
    public static final int BOUND_OUT = 1;

    /**
     * Returns a List of parameters the implementing instance uses.
     * Each list element is itself a List to describe the parameter as follows:
     *
     * - 0 : Parameter name
     * - 1 : Parameter description. Useful for GUI rendition
     * - 2 : Parameter type. Useful for GUI rendition.
     * - 3 : Required or Optional parameter. Useful for GUI validation.
     * - 4 : Parameter usage. Useful for GUI rendition.
     * - 5 : Default value
     *
     * @return List of Parameters for the implementing Transport.
     *
     *
     * @see com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder
     * @see com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder.ParameterDefinition
     * @see com.tacitknowledge.simulator.BaseConfigurable#parametersList
     */
    public List<List> getParametersList();

    /**
     *
     * @param parameters Configurable parameter values
     */
    public void setParameters(Map<String, String> parameters);

    /**
     *
     * @param bound Configurable bound
     * @param parameters Configurable parameter values
     */
    public void setBoundAndParameters(int bound, Map<String, String> parameters);

    /**
     * @return The bounding (IN or OUT) of the configurable instance
     */
    int getBound();
}
