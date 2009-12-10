package com.tacitknowledge.simulator.configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * DSL builder for a list of ParameterDefinition objects
 * @author galo
 */
public class ParametersListBuilder
{
    /**
     * The list to be built
     */
    private final List<ParameterDefinitionBuilder.ParameterDefinition> list;

    /**
     * Hide default constructor
     */
    private ParametersListBuilder()
    {
        list = new ArrayList<ParameterDefinitionBuilder.ParameterDefinition>();
    }

    /**
     * Initial DSL command
     * @return The instance to work with
     */
    public static ParametersListBuilder parameters()
    {
        return new ParametersListBuilder();
    }

    /**
     *
     * @param paramDef The ParameterDefinition to be added to the list
     * @return This instance
     */
    public ParametersListBuilder add(ParameterDefinitionBuilder.ParameterDefinition paramDef)
    {
        list.add(paramDef);
        return this;
    }

    /**
     * @return The built list
     */
    public List<ParameterDefinitionBuilder.ParameterDefinition> build()
    {
        return list;
    }
}
