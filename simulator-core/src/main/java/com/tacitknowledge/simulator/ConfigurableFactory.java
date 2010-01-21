package com.tacitknowledge.simulator;

import java.util.List;

/**
 * @author galo
 */
public interface ConfigurableFactory
{
    /**
     * Returns the Configurable implementation of the provided Configurable name.
     *
     * @param name The Configurable name.
     * @return Configurable implementation or null if the Configurable name is not supported.
     */
    Configurable getConfigurable(final String name);

    /**
     *
     * @param name The Configurable name.
     * @return The parameter descriptions list
     * @throws ConfigurableException If the parameters definition list is empty
     */
    List<List> getParametersDefinition(final String name) throws ConfigurableException;
}
