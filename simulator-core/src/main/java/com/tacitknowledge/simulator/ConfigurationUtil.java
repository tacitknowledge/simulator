package com.tacitknowledge.simulator;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

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
        if(null == className) {
            throw new ConfigurableException("Class name parameter is null");
        }
        Configurable configurable = null;

        if (classPool.containsKey(className))
        {
            configurable = classPool.get(className);
        }
        else
        {
            @SuppressWarnings("rawtypes")
            Class configurationClass;
            try
            {
                configurationClass = Class.forName(className);
                Object instance = configurationClass.newInstance();
                if (instance instanceof Configurable)
                {
                    configurable = (Configurable) instance;
                    classPool.put(className, configurable);
                }
            }
            catch (InstantiationException e)
            {
                throw new ConfigurableException("Unable to instantiate class " + className, e);
            }
            catch (IllegalAccessException e)
            {
                throw new ConfigurableException("Unable to access class " + className, e);
            }
            catch (ClassNotFoundException e)
            {
                throw new ConfigurableException(
                        "Unable to find configurable class " + className,
                        e);
            }

        }
        return configurable;
    }
    
    public static Map<String, String> getPropertiesMap(Properties properties)
    {
        Map<String, String> map = new HashMap<String, String>();

        for (Entry<?, ?> entry : properties.entrySet())
        {
            map.put(entry.getKey().toString(), entry.getValue().toString());
        }

        return map;
    }

}
