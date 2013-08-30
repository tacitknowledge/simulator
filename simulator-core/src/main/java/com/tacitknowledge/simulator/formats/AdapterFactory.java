package com.tacitknowledge.simulator.formats;

import java.util.Properties;

import com.tacitknowledge.simulator.*;

public class AdapterFactory
{
    public static Adapter createAdapter(int bound, String format, Properties properties)
    {
        final Configurable configurable = new BaseConfigurable(bound, ConfigurationUtil.getPropertiesMap(properties));

        if (FormatConstants.CSV.equals(format))
        {
            return new CsvAdapter(configurable);
        }

        if (FormatConstants.JSON.equals(format))
        {
            return new JsonAdapter(configurable);
        }

        if (FormatConstants.PLAIN_TEXT.equals(format))
        {
            return new PlainTextAdapter();
        }

        if (FormatConstants.PROPERTIES.equals(format))
        {
            return new PropertiesAdapter(configurable);
        }

        if (FormatConstants.REST.equals(format))
        {
            return new RestAdapter(configurable);
        }

        if (FormatConstants.SOAP.equals(format))
        {
            return new SoapAdapter(configurable);
        }

        if (FormatConstants.XML.equals(format))
        {
            return new XmlAdapter(configurable);
        }

        if (FormatConstants.YAML.equals(format))
        {
            return new YamlAdapter(configurable);
        }

        if (FormatConstants.DOC_LITERAL_SOAP.equals(format))
        {
            return new DocLiteralWrappedSoapAdapter(configurable);
        }

        if (FormatConstants.SOAP_FULL_RESPONSE.equals(format))
        {
            return new FullResponseSoapAdapter(configurable);
        }

        return null;
    }

}
