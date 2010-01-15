package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.FormatAdapterException;
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
     * @param o text String
     * @return
     * @throws FormatAdapterException
     */

    @Override
    public Map<String, Object> generateBeans(Exchange o) throws FormatAdapterException
    {

        String text = o.getIn().getBody(String.class);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("text", text);
        return map;
    }


    @Override
    public String adaptTo(Object scriptExecutionResult, Exchange exchange) throws FormatAdapterException
    {
        return scriptExecutionResult.toString();
    }

    /**
     * empty method.
     *
     * @throws ConfigurableException
     */
    @Override
    protected void validateParameters() throws ConfigurableException
    {

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
     * @see com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder.ParameterDefinition
     */
    @Override
    public List<List> getParametersList()
    {
        return null;
    }
}
