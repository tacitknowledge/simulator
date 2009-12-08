package com.tacitknowledge.simulator.scripting;

import com.tacitknowledge.simulator.SimulatorException;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Undefined;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: 07.12.2009
 * Time: 18:04:19
 * Javascript specific pojo populator
 *
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 */
public class JavaScriptObjectMapper implements ObjectMapper {

    private JavaObjectMapper javaMapper = new JavaObjectMapper();

    public Map<String, Object> getMapFromObject(Object o) throws SimulatorException {

        Map<String, Object> map = new HashMap<String, Object>();
        if (o instanceof NativeObject) {
            NativeObject nativeObject = (NativeObject) o;
            Object[] objects = nativeObject.getAllIds();
            for (Object object : objects) {
                String fieldName = object.toString();
                Object fieldValue = NativeObject.getProperty(nativeObject, fieldName);
                Object mapValue;
                if (fieldValue instanceof String || fieldValue instanceof Number) {
                    // --- If it's a String or a Number, use as it is
                    mapValue = fieldValue;
                } else {
                    if (fieldValue instanceof NativeArray) {
                        mapValue = getListFromArray((NativeArray) fieldValue);
                    } else {
                        mapValue = getMapFromObject(fieldValue);
                    }
                }
                map.put(fieldName, mapValue);
            }
        } else if (o instanceof NativeArray) {
            // --- If it's an Array, get a List from it
            List<Object> fromArray = getListFromArray((NativeArray) o);
            map.put(null, fromArray);

        } else if(o instanceof Undefined) {
            //todo what to do?
        }else {
            javaMapper.getMapFromObject(o);
        }
        return map;
    }

    private List<Object> getListFromArray(NativeArray array) throws SimulatorException {

        List<Object> list = new ArrayList<Object>();

        for (int i = 0; i < array.getLength(); i++) {
            Object object = array.get(i, null);
            if (object instanceof String || object instanceof Number) {
                list.add(object);
            }else{
              list.add(getMapFromObject(object));
            }

        }
        return list;
    }
}
