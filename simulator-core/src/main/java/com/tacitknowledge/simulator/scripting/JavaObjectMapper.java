package com.tacitknowledge.simulator.scripting;

import com.tacitknowledge.simulator.SimulatorException;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: 07.12.2009
 * Time: 18:09:48
 *
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 */
public class JavaObjectMapper implements ObjectMapper
{

    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(JavaObjectMapper.class);

    /**
     * Returns a Map from an object. Each attribute in the object will become a key in the Map.
     *
     * @param o The object to be mapped
     * @return The map representation of the passed object
     * @throws SimulatorException If anything goes wrong
     */
    public Map<String, Object> getMapFromObject(Object o) throws ObjectMapperException
    {
        Map<String, Object> map = new HashMap<String, Object>();

        // --- Iterate through the attributes
        for (Field field : o.getClass().getDeclaredFields())
        {
            String fieldName = field.getName();
            Object fieldValue = null;
            try
            {
                fieldValue = field.get(o);
            }
            catch (IllegalAccessException iae)
            {
                logger.error(iae.getMessage());
                throw new ObjectMapperException(
                    "Unexpected error accesing field value: " + iae.getMessage(),
                    iae);
            }

            // --- Depending on the value type, get it's representation
            Object mapValue;
            if (fieldValue instanceof String)
            {
                // --- If it's a String, use as it is
                mapValue = fieldValue;
            }
            else if (fieldValue.getClass().isArray())
            {
                // --- If it's an Array, get a List from it
                mapValue = getListFromArray((Object[]) fieldValue);
            }
            else
            {
                // --- By default, if it's not a String nor Array, we assume it's a custom class
                mapValue = getMapFromObject(fieldValue);
            }

            map.put(fieldName, mapValue);
        }

        return map;
    }

    /**
     * Returns a List from an array. The array will populated with either Maps or Strings,
     * depending on the Array contents
     *
     * @param objects The Array to be List-ified
     * @return The list populated with eithert Strings or the Map representation of its items
     * @throws SimulatorException If anything goes wrong
     */
    private List getListFromArray(Object[] objects) throws ObjectMapperException
    {
        List list = new ArrayList();
        for (Object o : objects)
        {
            if (o instanceof String)
            {
                list.add(o);
            }
            else
            {
                list.add(getMapFromObject(o));
            }
        }
        return list;
    }

}
