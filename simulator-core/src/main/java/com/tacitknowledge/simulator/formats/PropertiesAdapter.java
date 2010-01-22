package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.StructuredSimulatorPojo;
import com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder;
import static com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder.name;
import static com.tacitknowledge.simulator.configuration.ParametersListBuilder.parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.camel.Exchange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Implementation of the Adapter interface for the Properties format.
 * Properties should come in an "inheritance" structure.
 * e.g.:
 * employee.firstName=
 * employee.address.street=
 * employee.title
 * <p/>
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
     * Logger for this class.
     */
    private static Logger logger = LoggerFactory.getLogger(PropertiesAdapter.class);

    /**
     * Adapter parameters definition.
     */
    private List<ParameterDefinitionBuilder.ParameterDefinition> parametersList =
        parameters().
            add(
                name(PARAM_PROPERTY_SEPARATOR).
                    label("Property level separator (defaults to dot \".\")").
                    defaultValue(".")
            );

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
     * @param bound Configurable bound
     * @param parameters @see Adapter#parameters
     * @inheritDoc
     */
    public PropertiesAdapter(final int bound, final Map<String, String> parameters)
    {
        super(bound, parameters);
    }

    /**
     * @inheritDoc
     * @param exchange - Exchange object
     * @return SimulatorPojo
     * @throws FormatAdapterException - if generated pojo cannot be transformed
     */
    @Override
    protected SimulatorPojo createSimulatorPojo(final Exchange exchange)
        throws FormatAdapterException
    {
        String object = exchange.getIn().getBody(String.class);

        logger.debug("Attempting to generate SimulatorPojo from Properties content:\n{}", object);

        SimulatorPojo pojo = new StructuredSimulatorPojo();

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
                throw fae;
            }
        }

        logger.debug("Finished generating SimulatorPojo from Properties content");
        return pojo;
    }

    /**
     * @inheritDoc
     * @param simulatorPojo - SimulatorPojo instance
     * @param exchange The Camel exchange
     * @return representation of properties as string
     * @throws FormatAdapterException - If an exception occurs when converting a pojo into a string
     */
    @Override
    protected String getString(final SimulatorPojo simulatorPojo, Exchange exchange)
        throws FormatAdapterException
    {
        if (simulatorPojo.getRoot().isEmpty())
        {
            throw new FormatAdapterException("Simulator Pojo root is empty.");
        }

        return getPropertiesAsString("", simulatorPojo.getRoot());
    }

    /**
     * @throws ConfigurableException if any required parameter is missing
     * @inheritDoc
     */
    @Override
    protected void validateParameters() throws ConfigurableException
    {
        if (getParamValue(PARAM_PROPERTY_SEPARATOR) != null)
        {
            this.propertySeparator = getParamValue(PARAM_PROPERTY_SEPARATOR);
        }
    }

    /**
     * 
     * @param container
     * @param path
     * @param value
     * @throws FormatAdapterException
     */
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

    /**
     * 
     * @param path
     * @param map
     * @return
     */
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

    /**
     * Returns a List of parameters the implementing instance uses.
     * Each list element is itself a List to describe the parameter as follows:
     * <p/>
     * - 0 : Parameter name
     * - 1 : Parameter description. Useful for GUI rendition
     * - 2 : Parameter type. Useful for GUI rendition.
     * - 3 : Required or Optional parameter. Useful for GUI validation.
     * - 4 : Parameter usage. Useful for GUI rendition.
     * - 5 : Default value
     *
     * @return List of Parameters for the implementing Transport.
     * @see com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder
     * @see com.tacitknowledge.simulator
     *      .configuration.ParameterDefinitionBuilder.ParameterDefinition
     */
    @Override
    public List<List> getParametersList()
    {

        return getParametersDefinitionsAsList(parametersList);
    }
}
