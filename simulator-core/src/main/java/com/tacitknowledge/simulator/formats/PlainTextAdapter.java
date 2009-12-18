package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: 24.11.2009
 * Time: 12:35:12
 *
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 */
public class PlainTextAdapter extends BaseAdapter
{
    /**
     * Structured pojo root is 'text'
     *
     * @param o text String
     * @return
     * @throws FormatAdapterException
     */

    @Override
    public Map<String, Object> generateBeans(String o) throws FormatAdapterException
    {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("text", o);
        return map;
    }


    @Override
    public String adaptTo(Object scriptExecutionResult) throws FormatAdapterException
    {
        return scriptExecutionResult.toString();
    }

    /**
     * empty method.
     *
     * @throws FormatAdapterException
     */
    @Override
    void validateParameters() throws FormatAdapterException
    {

    }

    public List<List> getParametersList()
    {
        return getParametersDefinitionsAsList(
            new ArrayList<ParameterDefinitionBuilder.ParameterDefinition>());
    }
}
