package com.tacitknowledge.simulator;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author galo
 */
public class ConfigurableFactoryImpl implements ConfigurableFactory
{   
    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(ConfigurableFactoryImpl.class);

    /**
     * Container for the Configurables
     */
    private Map<String, Class> configurables = new HashMap<String, Class>();

    /**
     * Default constructor
     * @param configs - configuration properties
     */
    protected ConfigurableFactoryImpl(final Map<String, Class> configs)
    {
        this.configurables = configs;
    }

    /**
     * Returns the Configurable implementation of the provided Configurable name.
     *
     * @param name The Configurable name.
     * @return Configurable implementation or null if the Configurable name is not supported.
     */
    public Configurable getConfigurable(final String name)
    {
        Configurable configurable = null;

        if (!configurables.containsKey(name.toUpperCase()))
        {
            logger.error("No Configurable found for type " + name.toUpperCase());
        }
        else
        {
            Class configurableClass = configurables.get(name.toUpperCase());
            try
            {
                configurable = (Configurable) configurableClass.newInstance();
            }
            catch (Exception e)
            {
                logger.error("Unexpected error trying to instantiate configurable "
                        + configurableClass.getName(), e);
            }
        }
        return configurable;
    }

    /**
     * @param name The Configurable name.
     * @return The parameter descriptions list
     * @throws com.tacitknowledge.simulator.ConfigurableException
     *          If the parameters definition list is empty
     */
    @Override
    public List<List> getParametersDefinition(final String name) throws ConfigurableException
    {
        List<List> list = null;
        // --- Configurable types should have been set with all-capitals
        Configurable configurable = getConfigurable(name);
        if (configurable != null)
        {
            list = configurable.getParametersList();
        }
        return list;
    }
}
