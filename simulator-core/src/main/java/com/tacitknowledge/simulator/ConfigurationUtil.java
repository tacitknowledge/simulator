package com.tacitknowledge.simulator;

import java.util.HashMap;
import java.util.Map;

/**
 * @author galo
 */
public final class ConfigurationUtil
{

    /**
     * Container for the Configurables
     */
    private static Map<String, Configurable> classPool = new HashMap<String, Configurable>();

    /**
     * Default constructor
     */
    private ConfigurationUtil()
    {
        //empty
    }

    /**
     * Returns the Configurable implementation of the provided Configurable name.
     *
     * @param className The Configurable name.
     * @return Configurable implementation or null if the Configurable name is not supported.
     * @throws ConfigurableException if an error occurs
     */
    public static Configurable getConfigurable(final String className) throws ConfigurableException
    {
        Configurable configurable = null;

        if (classPool.containsKey(className))
        {
            configurable = classPool.get(className);
        }
        else
        {
            Class configurationClass = null;
            try
            {
                configurationClass = Class.forName(className);
                Object instance = configurationClass.newInstance();
                if (instance instanceof Configurable)
                {
                    configurable = (Configurable) instance;
                    classPool.put(className, configurable);
                }
                instance = null;
            }
            catch (InstantiationException e)
            {
                throw new ConfigurableException("Unable to instantiate class. ", e);
            }
            catch (IllegalAccessException e)
            {
                throw new ConfigurableException("Unable to access class. ", e);
            }
            catch (ClassNotFoundException e)
            {
                throw new ConfigurableException("Unable to find configurable class. ", e);
            }

        }
        return configurable;
    }

}
