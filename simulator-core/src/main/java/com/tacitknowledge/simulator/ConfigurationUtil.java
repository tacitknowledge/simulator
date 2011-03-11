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
     * Default constructor
     */
    private ConfigurationUtil()
    {}

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
