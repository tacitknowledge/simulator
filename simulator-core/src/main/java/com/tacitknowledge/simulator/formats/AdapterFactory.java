package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.ConfigurableFactoryImpl;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory implementation for the format adapters
 *
 * @see com.tacitknowledge.simulator.Adapter
 * @see com.tacitknowledge.simulator.Configurable
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class AdapterFactory extends ConfigurableFactoryImpl
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
    private static final Map<String, Class> adapters = new HashMap<String, Class>()
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
        super(adapters);
    }

    /**
     *
     * @return The singleton instance
     */
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
        return (Adapter<?>) getConfigurable(format);
    }
}
