package com.tacitknowledge.simulator;

import com.tacitknowledge.simulator.formats.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory implementation for the different adapters
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
class AdapterFactory
{
    /**
     * Container for the adapters.
     */
    private static Map<String, Adapter> adapters = new HashMap<String, Adapter>()
    {
        {
            put(FormatConstants.JSON, new JsonAdapter());
            put(FormatConstants.XML, new XmlAdapter());
            put(FormatConstants.CSV, new CsvAdapter());
            put(FormatConstants.YAML, new YamlAdapter());
            put(FormatConstants.PROPERTIES, new PropertiesAdapter());
        }
    };

    /**
     * Returns implementation of the adapter for the provided format.
     * @param format the format of the data.
     * @return Adapter for the specified format.
     */
    public static Adapter getAdapter(String format)
    {
        return adapters.get(format);
    }
}
