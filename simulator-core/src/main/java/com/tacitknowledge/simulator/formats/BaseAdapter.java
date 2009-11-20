package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.FormatAdapterException;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for Adapter implementations.
 *
 * @author galo
 */
public abstract class BaseAdapter implements Adapter<Object>
{
    /**
     * Line separator constant. Available for all adapters
     */
    protected static final String LINE_SEP = System.getProperty("line.separator");

    /**
     * The Adapter parameters. Each Adapter implementation should define its corresponding
     * parameters.
     */
    private Map<String, String> parameters = new HashMap<String, String>();

    /**
     * Constructor
     */
    public BaseAdapter()
    {
    }

    /**
     * Constructor
     *
     * @param parameters @see #parameters
     */
    public BaseAdapter(Map<String, String> parameters)
    {
        this.parameters = parameters;
    }


    /**
     * @inheritDoc
     * @param parameters The Adapter parameters Map
     */
    public void setParameters(Map<String, String> parameters)
    {
        this.parameters = parameters;
    }

    /**
     * Sets and/or overrides instance variables from provided parameters and validates that
     * the required parameters are present.
     * @throws FormatAdapterException If any required parameter is missing or incorrect
     */
    abstract void validateParameters() throws FormatAdapterException;

    /**
     * @param name The parameter name. Parameter names should be defined by each implementation.
     * @return The parameter value or null if not defined.
     */
    protected String getParamValue(String name)
    {
        return parameters.get(name);
    }
}
