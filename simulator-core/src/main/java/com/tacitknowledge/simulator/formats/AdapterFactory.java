package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.ConfigurableException;
import org.apache.log4j.Logger;

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
     * Singleton instance
     */
    private static AdapterFactory instance = null;

    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(AdapterFactory.class);

    /**
     * Container for the adapters.
     */
    private Map<String, Class> adapters = new HashMap<String, Class>()
    {
        {
            put(FormatConstants.JSON, JsonAdapter.class);
            put(FormatConstants.XML, XmlAdapter.class);
            put(FormatConstants.CSV, CsvAdapter.class);
            put(FormatConstants.YAML, YamlAdapter.class);
            put(FormatConstants.PROPERTIES, PropertiesAdapter.class);
            put(FormatConstants.PLAIN_TEXT, PlainTextAdapter.class);
            put(FormatConstants.REST, RestAdapter.class);
            put(FormatConstants.SOAP, SoapAdapter.class);
        }
    };

    /**
     * Hiding the default constructor
     */
    private AdapterFactory()
    {
    }

    public static AdapterFactory getInstance()
    {
        if (instance == null)
        {
            instance = new AdapterFactory();
        }
        return instance;
    }

    /**
     * Returns implementation of the adapter for the provided format.
     *
     * @param format The format of the data. @see com.tacitknowledge.simulator.FormatConstants
     * @return Adapter for the specified format or null if the format is not supported.
     */
    public Adapter<?> getAdapter(final String format)
    {
        Adapter<?> adapter = null;
        try
        {
            //adapter = ((Adapter<?>) adapters.get(format)).newInstance();

            adapter = (Adapter<?>) adapters.get(format.toUpperCase()).newInstance();
        }
        catch (Exception e)
        {
            logger.error("Unexpected error trying to instantiate adapter " + format +
                    ": " + e.getMessage());
        }
        return adapter;
    }

    /**
     * @param format The format the adapter is needed for
     * @return The parameter descriptions list
     * @see com.tacitknowledge.simulator.Adapter#getParametersList()
     *
     * @throws ConfigurableException If the parameters definition list is empty 
     */
    public List<List> getAdapterParameters(final String format) throws ConfigurableException
    {
        List<List> list = null;
        // --- Formats should have been set with all-capitals
        Adapter<?> adapter = getAdapter(format);
        if (adapter != null)
        {
            list = adapter.getParametersList();
        }

        return list;
    }

}
