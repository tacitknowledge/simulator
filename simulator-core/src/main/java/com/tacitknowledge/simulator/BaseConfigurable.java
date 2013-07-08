package com.tacitknowledge.simulator;

import java.util.HashMap;
import java.util.Map;

/**
 * @author galo
 */
public class BaseConfigurable implements Configurable
{
    /**
     * Bounding of the extending instance.
     * Defaults to IN (0)
     *
     * @see com.tacitknowledge.simulator.Configurable
     */
    private int bound = BOUND_IN;

    /**
     * The Configurable parameter values.
     * Each Configurable implementation should define its corresponding parameters.
     */
    private Map<String, String> parameters = new HashMap<String, String>();

    /**
     * Constructor
     */
    public BaseConfigurable()
    {
        this(Configurable.BOUND_IN,new HashMap<String, String>());
    }

    /**
     * Constructor
     * This should be the prefered constructor method from within JAVA.
     * @param parameters Parameter values
     */
    public BaseConfigurable(final Map<String, String> parameters)
    {
        this.bound = Configurable.BOUND_IN;
        this.parameters = parameters;
    }

    /**
     * This should be the second prefered constructor method from within JAVA.
     * @param bound Configurable bound
     * @param parameters Parameter values
     */
    public BaseConfigurable(final int bound, final Map<String, String> parameters)
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
     * @param name The parameter name. Parameter names should be defined by each implementation.
     * @return The parameter value or null if not defined.
     */
    public String getParamValue(final String name)
    {
        return parameters.get(name);
    }
    
    public void setParamValue(final String name, final String value)
    {
        parameters.put(name, value);
    }

    /**
     *
     * @return The provided parameters
     */
    public Map<String, String> getParameters()
    {
        return parameters;
    }
}
