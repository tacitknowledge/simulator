package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.Adapter;
import junit.framework.TestCase;

/**
 * @author galo
 */
public class AdapterFactoryTest extends TestCase
{
    public void testShouldGetNullWithWrongAdapterType()
    {
        assertNull(
                AdapterFactory.getInstance().getAdapter("SOMEADAPTER")
        );
    }

    public void testGetJsonAdapter()
    {
        assertTrue(
                AdapterFactory.getInstance().getAdapter(FormatConstants.JSON) instanceof
                        JsonAdapter);
    }

    public void testGetXmlAdapter()
    {
        assertTrue(
                AdapterFactory.getInstance().getAdapter(FormatConstants.XML) instanceof
                        XmlAdapter);
    }

    public void testGetCsvAdapter()
    {
        assertTrue(
                AdapterFactory.getInstance().getAdapter(FormatConstants.CSV) instanceof
                        CsvAdapter);
    }

    public void testGetYamlAdapter()
    {
        assertTrue(
                AdapterFactory.getInstance().getAdapter(FormatConstants.YAML) instanceof
                        YamlAdapter);
    }

    public void testGetPropertiesAdapter()
    {
        assertTrue(
                AdapterFactory.getInstance().getAdapter(FormatConstants.PROPERTIES) instanceof
                        PropertiesAdapter);
    }

    public void testGetTestAdapter()
    {
        assertTrue(
                AdapterFactory.getInstance().getAdapter(FormatConstants.REST) instanceof
                        RestAdapter);
    }

    public void testGetSoapAdapter()
    {
        assertTrue(
                AdapterFactory.getInstance().getAdapter(FormatConstants.SOAP) instanceof
                        SoapAdapter);
    }
}
