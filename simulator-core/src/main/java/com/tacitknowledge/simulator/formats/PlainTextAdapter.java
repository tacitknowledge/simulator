package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;

/**
 * Date: 24.11.2009
 * Time: 12:35:12
 *
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 */
public class PlainTextAdapter extends BaseAdapter
{
    /**
     * Adapter parameters definition.
     */
    private List<ParameterDefinitionBuilder.ParameterDefinition> parametersList =
            new ArrayList<ParameterDefinitionBuilder.ParameterDefinition>();

    /**
     * Structured pojo root is 'text'
     *
     * @param exchange text String
     * @return The generated custom beans map
     * @throws FormatAdapterException if an error occurs
     */

    @Override
    public Map<String, Object> generateBeans(final Exchange exchange) throws FormatAdapterException
    {
        String text = exchange.getIn().getBody(String.class);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("text", text);
        return map;
    }


    @Override
    public String adaptTo(final Object scriptExecutionResult, final Exchange exchange) throws
            FormatAdapterException
    {
        return scriptExecutionResult.toString();
    }

    /**
     * empty method.
     *
     * @throws ConfigurableException If any required parameter is missing
     */
    @Override
    protected void validateParameters() throws ConfigurableException
    {

    }

    /**
     * @param exchange The Camel exchange
     * @return The generated SimulatorPojo
     * @throws com.tacitknowledge.simulator.FormatAdapterException If any error occurs
     *
     */
    @Override
    protected SimulatorPojo createSimulatorPojo(final Exchange exchange)
        throws FormatAdapterException
    {
        return null;
    }

    /**
     * @param scriptExecutionResult The object returned by the scenario excecution script
     * @param exchange              The Camel exchange
     * @return A String object in the requested format representing the script result
     */
    @Override
    protected String getString(final SimulatorPojo scriptExecutionResult, final Exchange exchange)
    {
        return null;
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
     * @see com.tacitknowledge.simulator.configuration
     *          .ParameterDefinitionBuilder.ParameterDefinition
     */
    public List<List> getParametersList()
    {
        return null;
    }
}
