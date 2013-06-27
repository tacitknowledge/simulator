package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorPojo;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.camel.Exchange;
import org.codehaus.jackson.map.ObjectMapper;
import org.mozilla.javascript.NativeObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    public NativeObjectScriptingAdapter(Map<String, String> parameters) {
        super(parameters);    //To change body of overridden methods use File | Settings | File Templates.
    }

    protected NativeObjectScriptingAdapter(int bound, Map<String, String> parameters) {
        super(bound, parameters);    //To change body of overridden methods use File | Settings | File Templates.
    }


    /**
     * @param exchange The Camel exchange
     * @return A Map of custom-generated beans generated from the input data
     * @throws com.tacitknowledge.simulator.ConfigurableException  If any required parameter is missing.
     * @throws com.tacitknowledge.simulator.FormatAdapterException If any other error occurs.
     */
    public Map<String, Object> generateBeans(final Exchange exchange)
        throws ConfigurableException, FormatAdapterException
    {
        validateParameters();

        SimulatorPojo pojo = createSimulatorPojo(exchange);
        //here get the NativeObject
        BSFManager manager = new BSFManager();
        ObjectMapper mapper = new ObjectMapper();
        NativeObject nativeJavascriptObject = null;
        String simpleJSON = null;
        String key = null;
        try {
            //check the entry map. should have one value with a single String key as root
            Map.Entry<String,Object> myEntry = pojo.getRoot().entrySet().iterator().next();
            key = myEntry.getKey();
            simpleJSON = mapper.writeValueAsString(pojo.getRoot().get(key));
            manager.declareBean("data", simpleJSON, String.class);
            nativeJavascriptObject =
                    (NativeObject) manager.eval("javascript", "ugh.js", 0, 0, "var resp = eval('(' + data + ')');resp");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (BSFException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        Map<String,Object> result = new HashMap<String, Object>();
        result.put(key,nativeJavascriptObject);
        return result;
    }



}

