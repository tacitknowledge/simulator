package com.tacitknowledge.simulator.formats;

import java.util.Properties;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.ConfigurationUtil;

public class AdapterFactory
{
    public static Adapter<Object> createAdapter(int bound, String format, Properties properties)
    {
        //TODO: Need to refactor adapter types to have default constructor and 
        // replace IFs with a get from map
        if (FormatConstants.CSV.equals(format))
        {
            return new CsvAdapter(bound, ConfigurationUtil.getPropertiesMap(properties));
        }

        if (FormatConstants.JSON.equals(format))
        {
            return new JsonAdapter(bound, ConfigurationUtil.getPropertiesMap(properties));
        }

        if (FormatConstants.PLAIN_TEXT.equals(format))
        {
            // What is plain text adapter? :)
            return new PlainTextAdapter();
        }

        if (FormatConstants.PROPERTIES.equals(format))
        {
            return new PropertiesAdapter(bound, ConfigurationUtil.getPropertiesMap(properties));
        }

        if (FormatConstants.REST.equals(format))
        {
            return new RestAdapter(bound, ConfigurationUtil.getPropertiesMap(properties));
        }

        if (FormatConstants.SOAP.equals(format))
        {
            return new SoapAdapter(bound, ConfigurationUtil.getPropertiesMap(properties));
        }

        if (FormatConstants.XML.equals(format))
        {
            return new XmlAdapter(bound, ConfigurationUtil.getPropertiesMap(properties));
        }

        if (FormatConstants.YAML.equals(format))
        {
            return new YamlAdapter(bound, ConfigurationUtil.getPropertiesMap(properties));
        }

        return null;
    }

}
