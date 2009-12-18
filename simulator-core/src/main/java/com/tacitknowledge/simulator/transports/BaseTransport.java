package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.Transport;
import com.tacitknowledge.simulator.TransportException;
import com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base Transport class. Contains attributes common to all transport implementations
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public abstract class BaseTransport implements Transport
{
    /**
     * Ampersand constant
     */
    protected static final String AMP = "&";

    /**
     * The Transport parameters. Each Transport implementation should define its corresponding
     * parameters.
     */
    private Map<String, String> parameters = new HashMap<String, String>();

    /**
     * Transport type
     */
    private String type;

    /**
     * Constructor
     * The default constructor should not be called. Implementing classes must set
     * transport type.
     *
     * @see com.tacitknowledge.simulator.transports.TransportConstants
     */
    private BaseTransport()
    {
    }

    /**
     * Constructor. This constructor should be called from the implementing classes'
     * default constructor.
     *
     * @param type Constructor type
     */
    protected BaseTransport(String type)
    {
        this.type = type;
    }

    /**
     * Constructor.
     *
     * @param type       @see #type
     * @param parameters @see #parameters
     */
    protected BaseTransport(String type, Map<String, String> parameters)
    {
        this.type = type;
        this.parameters = parameters;
    }

    /**
     * Getter for @see #type
     *
     * @return @see #type
     */
    public String getType()
    {
        return type;
    }

    /**
     * @param parameters The Transport parameters Map
     * @inheritDoc
     */
    public void setParameters(Map<String, String> parameters)
    {
        this.parameters = parameters;
    }

    /**
     * Sets and/or overrides instance variables from provided parameters and validates that
     * the required parameters are present.
     *
     * @throws TransportException If any required parameter is missing or incorrect
     */
    abstract void validateParameters() throws TransportException;

    /**
     * @param name The parameter name. Parameter names should be defined by each implementation.
     * @return The parameter value or null if not defined.
     */
    protected String getParamValue(String name)
    {
        return parameters.get(name);
    }

    /**
     * Returns a List of ParameterDefinitions in their List representation
     *
     * @param parametersList The parameter definitions list
     * @return The list of lists
     */
    protected List<List> getParametersDefinitionsAsList(
        List<ParameterDefinitionBuilder.ParameterDefinition> parametersList)
    {
        List<List> list = new ArrayList<List>();
        for (ParameterDefinitionBuilder.ParameterDefinition param : parametersList)
        {
            list.add(param.getAsList());
        }
        return list;
    }

    @Override
    public String toString()
    {
        return "BaseTransport{" +
            "parameters=" + parameters +
            ", type='" + type + '\'' +
            '}';
    }
}
