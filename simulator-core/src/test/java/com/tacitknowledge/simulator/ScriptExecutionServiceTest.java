package com.tacitknowledge.simulator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import com.tacitknowledge.simulator.formats.JsonAdapter;
import org.apache.bsf.BSFManager;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.DefaultMessage;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;

import com.tacitknowledge.simulator.formats.XmlAdapter;
import com.tacitknowledge.simulator.scripting.ScriptException;
import com.tacitknowledge.simulator.scripting.ScriptExecutionService;
import org.mozilla.javascript.NativeObject;

/**
 * This test will help us to validate we are able to read and execute the different scripting
 * languages
 *
 * @author JOrge Galindo (jgalindo@tacitknowledge.com)
 */
public class ScriptExecutionServiceTest
{
    /**
     * This method will test the evaluation for JavaScript
     *
     * @throws Exception if there is a problem evaluating the script
     */
    @Test
    public void testEvalJavaScript() throws Exception
    {
        // --- First, get the SimulatorPojo from the data
        XmlAdapter adapter = new XmlAdapter();

        CamelContext context = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(context);
        Message message = new DefaultMessage();
        message.setBody(TestHelper.XML_DATA);
        exchange.setIn(message);

        // --- Get the data in a strcutured form as a SimulatorPojo
        Map<String, Object> beans = adapter.generateBeans(exchange);

        // --- Now, get a ScriptExecutionService and set the language (javascript)
        ScriptExecutionService execServ = new ScriptExecutionService("javascript");

        // --- Test a couple of values
        Object result = execServ.eval("employees", "Get employees", beans);
        assertNotNull(result);

        result = execServ.eval("employees.reportDate", "Is it today?", beans);
        assertEquals(result, "2009-11-05");

        result = execServ.eval("employees.employee[1].name", "Second employee name", beans);
        assertEquals(result, "Sara");
    }

    /**
     * This test will validate that we are able to execute ruby code
     *
     * @throws ScriptException if there is a problem with the language evaluation
     */
    @Test
    public void testEvalRuby() throws ScriptException
    {
        String myScript = "puts 'Hello Ruby'\n return 'hello'";
        ScriptExecutionService execServ = new ScriptExecutionService("ruby");
        Object result = execServ.eval(myScript, "myScript.rb", null);
        assertEquals("hello", result.toString());
    }

    @Test
    public void testEvalJavaScriptAndJSON() throws Exception
    {
        // --- First, get the SimulatorPojo from the data
        JsonAdapter adapter = new JsonAdapter();

        CamelContext context = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(context);
        Message message = new DefaultMessage();
        message.setBody(TestHelper.JSON_DATA);
        exchange.setIn(message);

        //assuming we convert everything to a big map, we should be able to convert that Map to JSON
        BSFManager manager = new BSFManager();
        final String simpleJSON = "{'firstName' : 'matthew', 'lastName' : 'short' }";
        //next PreProcess the JSON into JavaScript Objects (org.mozilla.javascript.NativeObject)
        manager.declareBean("data", simpleJSON, String.class);
        Object nativeJavascriptObject = manager.eval("javascript", "ugh.js", 0, 0, "var resp = eval('(' + data + ')');resp");
        //Now make our native object available for the real script
        manager.declareBean("obj", nativeJavascriptObject, NativeObject.class);
        Object result = manager.eval("javascript", "ugh.js", 0, 0, "obj.firstName == 'matthew'");
        assertTrue("Should be matthew", (Boolean )result);

    }
//{"root":{"obj":{"response":{"statusCode":"200","body":"","contentType":"text/html"},"request":{"headers":{"Host":"127.0.0.1:8030","User-Agent":"Apache-HttpClient/4.2.5 (java 1.5)","Connection":"Keep-Alive"},"method":"GET","params":{"Address":"TW9"}}}}}
    @Test
    public void testEvalJavaScriptAndJSONTwo() throws Exception
    {
        // --- First, get the SimulatorPojo from the data

        Map<String,Object> myMap = new HashMap<String, Object>();
        myMap.put("firstName","matthew");
        myMap.put("lastName","short");
        ObjectMapper mapper = new ObjectMapper();
        String simpleJSON = null;
        simpleJSON = mapper.writeValueAsString(myMap);
        //assuming we convert everything to a big map, we should be able to convert that Map to JSON
        BSFManager manager = new BSFManager();
//        final String simpleJSON = "{\"root\":{\"obj\":{\"response\":{\"statusCode\":\"200\",\"body\":\"\",\"contentType\":\"text/html\"},\"request\":{\"headers\":{\"Host\":\"127.0.0.1:8030\",\"User-Agent\":\"Apache-HttpClient/4.2.5 (java 1.5)\",\"Connection\":\"Keep-Alive\"},\"method\":\"GET\",\"params\":{\"Address\":\"TW9\"}}}}}";
//        final String simpleJSON = "{'firstName' : 'matthew','lastName' : 'short'}";

        //next PreProcess the JSON into JavaScript Objects (org.mozilla.javascript.NativeObject)
        manager.declareBean("data", simpleJSON, String.class);
        NativeObject nativeJavascriptObject = (NativeObject) manager.eval("javascript", "ugh.js", 0, 0, "var resp = eval('(' + data + ')');resp");
        //Now make our native object available for the real script
        manager.declareBean("obj", nativeJavascriptObject, NativeObject.class);
        Object result = manager.eval("javascript", "ugh.js", 0, 0, "obj.firstName;");
        assertEquals("Should be matthew","matthew",result);
        //obj.root.obj.request.params.Address == 'TW9'

    }
//

}
