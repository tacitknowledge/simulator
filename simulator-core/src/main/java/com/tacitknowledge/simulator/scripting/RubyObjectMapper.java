package com.tacitknowledge.simulator.scripting;

import org.jruby.RubyArray;
import org.jruby.RubyHash;
import org.jruby.RubyNil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Ruby specific pojo populator
 *
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 */
public class RubyObjectMapper implements ObjectMapper
{
    /**
     * The Java Object Mapper
     */
    private JavaObjectMapper javaMapper = new JavaObjectMapper();

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getMapFromObject(final Object o) throws ObjectMapperException
    {
        Map<String, Object> map = new HashMap<String, Object>();
        if (o instanceof RubyHash)
        {
            RubyHash rubyHash = (RubyHash) o;
            List<String> variableNames = rubyHash.getVariableNameList();


            for (String fieldName : variableNames)
            {

                Object fieldValue = rubyHash.get(fieldName);
                Object mapValue;
                if (fieldValue instanceof String || fieldValue instanceof Number)
                {
                    // --- If it's a String or a Number, use as it is
                    mapValue = fieldValue;
                }
                else
                {
                    if (fieldValue instanceof RubyArray)
                    {
                        mapValue = getListFromArray((RubyArray) fieldValue);
                    }
                    else
                    {
                        mapValue = getMapFromObject(fieldValue);
                    }
                }
                map.put(fieldName, mapValue);
            }
        }
        else if (o instanceof RubyArray)
        {
            // --- If it's an Array, get a List from it
            List<Object> fromArray = getListFromArray((RubyArray) o);
            map.put(null, fromArray);

        }
        else if (o instanceof RubyNil)
        {
            //TODO: what to do?
        }
        else
        {
            javaMapper.getMapFromObject(o);
        }
        return map;
    }

    /**
     * Returns a List from an array. The array will be populated with either Maps or Strings,
     * depending on the Array contents
     *
     * @param rubyArray The Array to be List-ified
     * @return The list populated with eithert Strings or the Map representation of its items
     * @throws ObjectMapperException If anything goes wrong
     */
    private List<Object> getListFromArray(final RubyArray rubyArray) throws ObjectMapperException
    {
        List<Object> list = new ArrayList<Object>();

        for (int i = 0; i < rubyArray.getLength(); i++)
        {
            Object object = rubyArray.get(i);
            if (object instanceof String || object instanceof Number)
            {
                list.add(object);
            }
            else
            {
                list.add(getMapFromObject(object));
            }

        }
        return list;
    }
}
