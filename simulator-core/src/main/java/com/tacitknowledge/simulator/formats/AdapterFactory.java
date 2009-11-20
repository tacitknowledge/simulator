package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.Adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory implementation for the format adapters
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class AdapterFactory
{
    /**
     * Hiding the default constructor
     */
    private AdapterFactory()
    {
    }
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
     *
     * @param format The format of the data. @see com.tacitknowledge.simulator.FormatConstants
     * @return Adapter for the specified format or null if the format is not supported.
     */
    public static Adapter< ? > getAdapter(String format)
    {
        return adapters.get(format);
    }

    /**
     * @see com.tacitknowledge.simulator.Adapter#getParametersList()
     * @param format The format the adapter is needed for
     * @return The parameter descriptions list
     */
    public static List<List> getAdapterParameters(String format)
    {
        List<List> list = null;
        // --- Formats should have been set with all-capitals
        if (adapters.get(format.toUpperCase()) != null)
        {
            list = adapters.get(format.toUpperCase()).getParametersList();
        }

        return list;
    }
}
