package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorPojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the Adapter interface for the Properties format.
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class PropertiesAdapter extends BaseAdapter implements Adapter<Object>
{
    /**
     * Adapter parameters definition.
     */
    private static List<List> parametersList = new ArrayList<List>();

    /**
     * @inheritDoc
     */
    public PropertiesAdapter()
    {
    }

    /**
     * @inheritDoc
     * @param parameters @see Adapter#parameters
     */
    public PropertiesAdapter(Map<String, String> parameters)
    {
        super(parameters);
    }

    /**
     * @inheritDoc
     * @param object @see Adapter#adaptFrom
     * @return @see Adapter#adaptFrom
     * @throws com.tacitknowledge.simulator.FormatAdapterException @see Adapter#adaptFrom
     */
    public SimulatorPojo adaptFrom(Object object) throws FormatAdapterException
    {
        //TODO Implement this functionality.
        return null;
    }

    /**
     * @inheritDoc
     * @param pojo @see Adapter#adaptTo
     * @return @see Adapter#adaptTo
     * @throws com.tacitknowledge.simulator.FormatAdapterException @see Adapter#adaptTo
     */
    public Object adaptTo(SimulatorPojo pojo) throws FormatAdapterException
    {
        //TODO Implement this functionality.
        return null;
    }

    /**
     * @inheritDoc
     * @return @see Adapter#getParametersList
     */
    public List<List> getParametersList()
    {
        return parametersList;
    }

    /**
     * @inheritDoc
     * @throws FormatAdapterException if any required parameter is missing
     */
    @Override
    void validateParameters() throws FormatAdapterException
    {

    }
}
