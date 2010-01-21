package com.tacitknowledge.simulator.configuration;

import java.util.ArrayList;

/**
 * DSL builder for a list of ParameterDefinition objects, extending an ArrayList.
 *
 * @author galo
 */
public class ParametersListBuilder extends ArrayList
{
    /**
     * Initial DSL command
     *
     * @return The instance to work with
     */
    public static ParametersListBuilder parameters()
    {
        return new ParametersListBuilder();
    }

    /**
     * @param paramDef The ParameterDefinition to be added to the list
     * @return This instance
     */
    public ParametersListBuilder add(final ParameterDefinitionBuilder.ParameterDefinition paramDef)
    {
        super.add(paramDef);
        return this;
    }
}
