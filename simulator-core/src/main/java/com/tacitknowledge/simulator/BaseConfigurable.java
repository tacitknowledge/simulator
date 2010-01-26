package com.tacitknowledge.simulator;

import com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author galo
 */
public abstract class BaseConfigurable implements Configurable
{
    /**
     * Bounding of the extending instance.
     * Defaults to IN (0)
     *
     * @see com.tacitknowledge.simulator.Configurable
     */
    private int bound = BOUND_IN;

    /**
     * Configurable parameters definition.
     * Should be set by the implementing Configurable.
     */
    private List<ParameterDefinitionBuilder.ParameterDefinition> parametersList = null;

    /**
     * The Configurable parameter values.
     * Each Configurable implementation should define its corresponding parameters.
     */
    private Map<String, String> parameters = new HashMap<String, String>();

    /**
     * Constructor
     */
    protected BaseConfigurable()
    {
    }

    /**
     * Constructor
     * This should be the prefered constructor method from within JAVA.
     * @param parameters Parameter values
     */
    protected BaseConfigurable(final Map<String, String> parameters)
    {
        this.bound = Configurable.BOUND_IN;
        this.parameters = parameters;
    }

    /**
     * This should be the second prefered constructor method from within JAVA.
     * @param bound Configurable bound
     * @param parameters Parameter values
     */
    protected BaseConfigurable(final int bound, final Map<String, String> parameters)
    {
        this.bound = bound;
        this.parameters = parameters;
    }

    /**
     * @return The bounding (IN or OUT) of the configurable instance
     */
    public int getBound()
    {
        return this.bound;
    }

    /**
     * Validate that all the required parameters have been provided.
     *
     * @throws ConfigurableException If any required parameter has not been set.
     */
    protected abstract void validateParameters() throws ConfigurableException;

    /**
     * @inheritDoc
     * @param parameters Configurable parameter values
     */
    public void setParameters(final Map<String, String> parameters)
    {
        this.parameters = parameters;
    }

    /**
     * @inheritDoc
     * @param bnd Configurable bound
     * @param param Configurable parameter values
     */
    public void setBoundAndParameters(final int bnd, final Map<String, String> param)
    {
        this.bound = bnd;
        this.parameters = param;
    }

    /**
     * Returns a List of ParameterDefinitions in their List representation
     *
     * @param paramsList The parameter definitions list
     * @return The list of lists
     */
    protected List<List> getParametersDefinitionsAsList(
        final List<ParameterDefinitionBuilder.ParameterDefinition> paramsList)
    {
        List<List> list = new ArrayList<List>();
        for (ParameterDefinitionBuilder.ParameterDefinition param : paramsList)
        {
            list.add(param.getAsList());
        }
        return list;
    }


    /**
     * @param name The parameter name. Parameter names should be defined by each implementation.
     * @return The parameter value or null if not defined.
     */
    protected String getParamValue(String name)
    {
        return parameters.get(name);
    }

    /**
     *
     * @return The provided parameters
     */
    protected Map<String, String> getParameters()
    {
        return parameters;
    }
}
