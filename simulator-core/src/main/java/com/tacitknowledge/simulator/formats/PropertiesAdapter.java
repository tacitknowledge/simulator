package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.StructuredSimulatorPojo;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Implementation of the Adapter interface for the Properties format.
 * Properties should come in an "inheritance" structure.
 * e.g.:
 *      employee.firstName=
 *      employee.address.street=
 *      employee.title
 *
 * Only one "root" object should exist.
 * Duplicate property names are not allowed and would throw an error.
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class PropertiesAdapter extends BaseAdapter implements Adapter<Object>
{
    // --- Adapter parameters
    /**
     * Property level separator parameter name. OPTIONAL.
     * Defaults to dot (".")
     */
    public static final String PARAM_PROPERTY_SEPARATOR = "propertySeparator";

    /**
     * Adapter parameters definition.
     */
    private List<List> parametersList = new ArrayList<List>() {
        {
            add(new ArrayList<String>()
            {
                {
                    add(PARAM_PROPERTY_SEPARATOR);
                    add("Property level separator (defaults to dot \".\")");
                    add("string");
                    add("optional");
                }
            });
        }
    };

    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(PropertiesAdapter.class);

    /**
     * @see #PARAM_PROPERTY_SEPARATOR
     */
    private String propertySeparator = ".";

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

    protected SimulatorPojo createSimulatorPojo(String object)
            throws FormatAdapterException
    {
        SimulatorPojo pojo = new StructuredSimulatorPojo();

        // --- The SimulatorPojo's root will contain only one key (csvContent) with a List value
        List rowObjects = new ArrayList();

        // --- First, split the incoming data into lines
        Pattern p = Pattern.compile("$", Pattern.MULTILINE);
        String[] rows = p.split(object);

        // --- Iterate through the lines
        for (String rowString : rows)
        {
            // --- Get property name/path and value
            String[] propNameValue = rowString.split("=");

            // --- Check the property separator in case we're using dot (.)
            String splitterRegEx = this.propertySeparator;
            if (this.propertySeparator.equals("."))
            {
                splitterRegEx = "\\" + this.propertySeparator;
            }

            List<String> propName =
                    new ArrayList<String>(
                            Arrays.asList(propNameValue[0].split(splitterRegEx)));
            String propValue = propNameValue[1];

            try
            {
                setPropertyToMap(pojo.getRoot(), propName, propValue);
            }
            catch (FormatAdapterException fae)
            {
                logger.error("Unexpected error processing property line: " + rowString);
                logger.error("Error received is: " + fae.getMessage());
                throw fae;
            }
        }
        return pojo;
    }


    protected String getString(SimulatorPojo simulatorPojo)
            throws FormatAdapterException
    {

        if (simulatorPojo.getRoot().isEmpty())
        {
            throw new FormatAdapterException("Simulator Pojo root is empty.");
        }

        return getPropertiesAsString("", simulatorPojo.getRoot());
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
        if (getParamValue(PARAM_PROPERTY_SEPARATOR) != null)
        {
            this.propertySeparator = getParamValue(PARAM_PROPERTY_SEPARATOR);
        }
    }

    private void setPropertyToMap(Map<String, Object> container, List<String> path, String value)
        throws FormatAdapterException
    {
        // --- Get the "current" path name.
        // Make sure to remove any potential line-break or space character
        String current = path.remove(0).trim();

        // --- Check if current exists as a key in the container map already
        if (container.containsKey(current))
        {
            // --- Get the key's value
            Object keyValue = container.get(current);

            // --- Check its instance
            if (keyValue instanceof Map)
            {
                // --- If the keyValue is a Map, there should more path elements
                if (path.size() > 0)
                {
                    // --- So, go down the tree
                    setPropertyToMap((Map<String, Object>) keyValue, path, value);
                }
                else
                {
                    // --- If there are no path entries left, something is wrong
                    throw new FormatAdapterException(
                            "Expecting either leaf path name or further path declaration. " +
                                    "Current path name: " + current);
                }
            }
            else if (keyValue instanceof String)
            {
                // --- If the instance is a String, we got a duplicate property name
                throw new FormatAdapterException(
                        "Duplicate property name. Current path name: " + current
                );
            }
        }
        else
        {
            // --- If it's a new key and...
            if (path.size() > 0)
            {
                // --- ...there are more path entries, create a new Map
                Map currentValue = new HashMap<String, Object>();
                // --- Go down
                setPropertyToMap(currentValue, path, value);
                // --- And set the map into the current key
                container.put(current, currentValue);
            }
            else
            {
                // --- ...this is a lead name, set the value in the current key
                container.put(current, value);
            }
        }
    }

    private String getPropertiesAsString(String path, Map<String, Object> map)
    {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, Object> entry : map.entrySet())
        {
            String key = entry.getKey();
            Object value = entry.getValue();
            String fullPath = path + entry.getKey();

            // --- Check the value instance
            if (value instanceof Map)
            {
                // --- If it's a Map, append the key to the current path and go down
                sb.append(
                        getPropertiesAsString(
                                fullPath + this.propertySeparator,
                                (Map<String, Object>) value));
            }
            else
            {
                // --- If it's a string, get property's string representation
                sb.append(fullPath + "=" + value + LINE_SEP);
            }
        }
        return sb.toString();
    }
}
