package com.tacitknowledge.simulator.formats;

import java.util.HashMap;
import java.util.Map;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.camel.Exchange;
import org.mozilla.javascript.IdScriptableObject;

import com.google.gson.Gson;
import com.tacitknowledge.simulator.Configurable;
import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorPojo;
//import org.codehaus.jackson.map.ObjectMapper;

/**
 * Created by IntelliJ IDEA.
 * User: mshort
 * Date: 6/27/13
 * Time: 8:46 AM
 * This is a temporary subclass to aid in refactoring adapters away from generated classes and to
 * JavaScript NativeObjects
 */
abstract public class NativeObjectScriptingAdapter extends BaseAdapter{

    public NativeObjectScriptingAdapter() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public NativeObjectScriptingAdapter(Configurable configurable) {
        super(configurable);
    }


    /**
     * @param exchange The Camel exchange
     * @return A Map of custom-generated beans generated from the input data
     * @throws com.tacitknowledge.simulator.ConfigurableException  If any required parameter is missing.
     * @throws com.tacitknowledge.simulator.FormatAdapterException If any other error occurs.
     */
    @Override
    public Map<String, Object> adaptForInput(final Exchange exchange)
        throws ConfigurableException, FormatAdapterException
    {
        validateParameters();

        SimulatorPojo pojo = createSimulatorPojo(exchange);
        //here get the NativeObject
        BSFManager manager = new BSFManager();
//        ObjectMapper mapper = new ObjectMapper();
		IdScriptableObject nativeJavascriptObject = null;
        String simpleJSON = null;
        String key = null;
        try {
            //check the entry map. should have one value with a single String key as root
            Map.Entry<String,Object> myEntry = pojo.getRoot().entrySet().iterator().next();
            key = myEntry.getKey();
            Gson gson = new Gson();

            final Object value = pojo.getRoot().get(key);
//            JSONObject converter = new JSONObject(value);
//            simpleJSON = mapper.writeValueAsString(pojo.getRoot().get(key));
            simpleJSON = gson.toJson(value);
            manager.declareBean("data", simpleJSON, String.class);
			nativeJavascriptObject = (IdScriptableObject) manager.eval(
			        "javascript", "ugh.js", 0, 0,
			        "var resp = eval('(' + data + ')');resp");
        } catch (BSFException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        Map<String,Object> result = new HashMap<String, Object>();
        result.put(key,nativeJavascriptObject);
        return result;
    }



}

