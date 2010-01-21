package com.tacitknowledge.simulator.scripting;

import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Undefined;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Javascript specific pojo populator
 *
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 */
public class JavaScriptObjectMapper implements ObjectMapper
{

    /**
     * Java object mapper
     */
    private JavaObjectMapper javaMapper = new JavaObjectMapper();

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getMapFromObject(final Object thisObject) throws
            ObjectMapperException
    {

        Map<String, Object> map = new HashMap<String, Object>();
        if (thisObject instanceof NativeObject)
        {
            NativeObject nativeObject = (NativeObject) thisObject;
            Object[] objects = nativeObject.getAllIds();
            for (Object object : objects)
            {
                String fieldName = object.toString();
                Object fieldValue = NativeObject.getProperty(nativeObject, fieldName);
                Object mapValue;
                if (fieldValue instanceof String || fieldValue instanceof Number)
                {
                    // --- If it's a String or a Number, use as it is
                    mapValue = fieldValue;
                }
                else
                {
                    if (fieldValue instanceof NativeArray)
                    {
                        mapValue = getListFromArray((NativeArray) fieldValue);
                    }
                    else
                    {
                        mapValue = getMapFromObject(fieldValue);
                    }
                }
                map.put(fieldName, mapValue);
            }
        }
        else if (thisObject instanceof NativeArray)
        {
            // --- If it's an Array, get a List from it
            List<Object> fromArray = getListFromArray((NativeArray) thisObject);
            map.put(null, fromArray);

        }
        else if (thisObject instanceof Undefined)
        {
            //todo what to do?
        }
        else
        {
            javaMapper.getMapFromObject(thisObject);
        }
        return map;
    }

    /**
     * Returns a List from an array. The array will be populated with either Maps or Strings,
     * depending on the Array contents
     *
     * @param array The Array to be List-ified
     * @return The list populated with eithert Strings or the Map representation of its items
     * @throws ObjectMapperException If anything goes wrong
     */
    private List<Object> getListFromArray(final NativeArray array) throws ObjectMapperException
    {

        List<Object> list = new ArrayList<Object>();

        for (int i = 0; i < array.getLength(); i++)
        {
            Object object = array.get(i, null);
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
