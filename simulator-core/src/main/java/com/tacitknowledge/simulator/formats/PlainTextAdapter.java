package com.tacitknowledge.simulator.formats;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;

import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorPojo;

/**
 * Date: 24.11.2009
 * Time: 12:35:12
 *
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 */
public class PlainTextAdapter extends NativeObjectScriptingAdapter
{
    /**
     * Structured pojo root is 'text'  This does not need conversion to NativeObject
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
}
