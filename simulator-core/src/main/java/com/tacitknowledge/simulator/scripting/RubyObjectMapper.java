package com.tacitknowledge.simulator.scripting;

import org.jruby.RubyArray;
import org.jruby.RubyHash;
import org.jruby.RubyNil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nikitabelenkiy
 */
public class RubyObjectMapper implements ObjectMapper
{

    private JavaObjectMapper javaMapper = new JavaObjectMapper();

    public Map<String, Object> getMapFromObject(Object o) throws ObjectMapperException
    {
        Map<String, Object> map = new HashMap<String, Object>();
        if (o instanceof RubyHash) {
            RubyHash rubyHash = (RubyHash) o;
            List<String> variableNames = rubyHash.getVariableNameList();


            for (String fieldName : variableNames) {

                Object fieldValue = rubyHash.get(fieldName);
                Object mapValue;
                if (fieldValue instanceof String || fieldValue instanceof Number) {
                    // --- If it's a String or a Number, use as it is
                    mapValue = fieldValue;
                } else {
                    if (fieldValue instanceof RubyArray) {
                        mapValue = getListFromArray((RubyArray) fieldValue);
                    } else {
                        mapValue = getMapFromObject(fieldValue);
                    }
                }
                map.put(fieldName, mapValue);
            }
        } else if (o instanceof RubyArray) {
            // --- If it's an Array, get a List from it
            List<Object> fromArray = getListFromArray((RubyArray) o);
            map.put(null, fromArray);

        } else if(o instanceof RubyNil) {
            //todo what to do?
        }else {
            javaMapper.getMapFromObject(o);
        }
        return map;
    }

    private List<Object> getListFromArray(RubyArray rubyArray) throws ObjectMapperException
    {
         List<Object> list = new ArrayList<Object>();

        for (int i = 0; i < rubyArray.getLength(); i++) {
            Object object = rubyArray.get(i);
            if (object instanceof String || object instanceof Number) {
                list.add(object);
            }else{
              list.add(getMapFromObject(object));
            }

        }
        return list;
    }
}
