package com.tacitknowledge.simulator.camel;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.ConversationScenario;
import com.tacitknowledge.simulator.TestHelper;
import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.formats.JsonAdapter;
import com.tacitknowledge.simulator.formats.PlainTextAdapter;
import com.tacitknowledge.simulator.formats.XmlAdapter;
import com.tacitknowledge.simulator.impl.ConversationScenarioImpl;
import com.tacitknowledge.simulator.impl.ConversationImpl;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultMessage;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class for ScenarioExecutionWrapper
 *
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 */
public class ScenarioExecutionWrapperTest
{
    private Conversation conversation;

    @Test
    public void testWithoutScenarios() throws Exception
    {
        conversation = new ConversationImpl(1, "Conversation1", null, null, new PlainTextAdapter(), new PlainTextAdapter(), null);
        conversation.addOrUpdateScenario(1, "javascript", "true", "text");

        ScenarioExecutionWrapper wrapper = new ScenarioExecutionWrapper(conversation);

        String testString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><xxxxxx/>";
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        Message message = new DefaultMessage();
        message.setBody(testString);
        exchange.setIn(message);
        String s = wrapper.process(exchange);
        Assert.assertNotNull(s);
        Assert.assertSame(testString, s);
    }

    @Test
    public void testWithOneScenario() throws Exception
    {

        String criteria = "employees.employee[0].name=='John';";
        String execution = "employees.employee[0].name='John12345';employees";

        conversation = new ConversationImpl(1, "Conversation1", null, null, new XmlAdapter(), new XmlAdapter(), null);
        conversation.addOrUpdateScenario(0, "javascript", criteria, execution);

        ScenarioExecutionWrapper wrapper = new ScenarioExecutionWrapper(conversation);

        CamelContext context = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(context);
        Message message = new DefaultMessage();
        message.setBody(TestHelper.XML_DATA);
        exchange.setIn(message);
        String s = wrapper.process(exchange);
        Assert.assertTrue(s.contains("John12345"));

        //modify the script and see what happens
        conversation.addOrUpdateScenario(0, "javascript", criteria, "employees.employee[0].name='John1234544444444';" +
            "employees" );

        exchange = new DefaultExchange(context);
        message = new DefaultMessage();
        message.setBody(TestHelper.XML_DATA);
        exchange.setIn(message);
        s = wrapper.process(exchange);

        Assert.assertTrue(s.contains("John1234544444444"));
    }

    @Test
    public void testTreeScenarios() throws Exception
    {
        conversation = new ConversationImpl(2, "Conversation2", null, null, new XmlAdapter(), new XmlAdapter(), null);

        String criteria1 = "employees.employee[0].name=='John12345';"; //false
        String execution1 = "employees.employee[0].name='Johnffff';employees";

        String criteria2 = "employees.employee[0].name=='John';";      //true
        String execution2 = "employees.employee[0].name='Johnaaaa';employees";  //this script should be executed

        String criteria3 = "employees.employee[0].name=='Johnffff';";      //false
        String execution3 = "employees.employee[0].name='John12345';employees";

        conversation.addOrUpdateScenario(0, "javascript", criteria1, execution1);
        conversation.addOrUpdateScenario(1, "javascript", criteria2, execution2);
        conversation.addOrUpdateScenario(2, "javascript", criteria3, execution3);

        ScenarioExecutionWrapper wrapper = new ScenarioExecutionWrapper(conversation);

        CamelContext context = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(context);
        Message message = new DefaultMessage();
        message.setBody(TestHelper.XML_DATA);
        exchange.setIn(message);

        String s = wrapper.process(exchange);
        Assert.assertFalse(s.contains("Johnffff"));
        Assert.assertFalse(s.contains("John12345"));
        Assert.assertTrue(s.contains("Johnaaaa"));

    }


    @Test
    public void testDifferentAdapters() throws Exception
    {


        String criteria2 = "employees.employee[0].name=='John';";      //true
        String execution2 = "employees.employee[0].name='Johnaaaa';employees";  //this script should be executed

        JsonAdapter outAdapter = new JsonAdapter();
        Map<String, String> param = new HashMap<String, String>();
        param.put(JsonAdapter.PARAM_JSON_CONTENT, "employees");
        outAdapter.setParameters(param);

        conversation = new ConversationImpl(2, "Conversation2", null, null, new XmlAdapter(), outAdapter, null);

        conversation.addOrUpdateScenario(0, "javascript", criteria2, execution2);

        ScenarioExecutionWrapper wrapper = new ScenarioExecutionWrapper(conversation);

        CamelContext context = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(context);
        Message message = new DefaultMessage();
        message.setBody(TestHelper.XML_DATA);
        exchange.setIn(message);

        String s = wrapper.process(exchange);
        Assert.assertFalse(s.contains("Johnffff"));
        Assert.assertFalse(s.contains("John12345"));
        Assert.assertTrue(s.contains("Johnaaaa"));

        // --- Test that the returned String is a valid JSON format
        try
        {
            JSONObject json = new JSONObject(s);
        }
        catch (JSONException je)
        {
            je.printStackTrace();
            Assert.fail("Returned string is not a valid JSON format: " + je.getMessage());
        }
    }

    @Test
    public void testRubyScenario() throws Exception
    {

        conversation = new ConversationImpl(2, "Conversation2", null, null, new XmlAdapter(), new XmlAdapter(), null);

        conversation.addOrUpdateScenario(0, "ruby", "require 'java'\n$employees.employee[0].name == 'John';", "$employees.employee[0].name='John12345';" +
                "$employees" );

        ScenarioExecutionWrapper wrapper = new ScenarioExecutionWrapper(conversation);

        CamelContext context = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(context);
        Message message = new DefaultMessage();
        message.setBody(TestHelper.XML_DATA);
        exchange.setIn(message);

        String s = wrapper.process(exchange);
        Assert.assertTrue(s.contains("John12345"));
    }

    @Test
    public void testReturnDifferentObject() throws Exception
    {
        conversation = new ConversationImpl(2, "Conversation2", null, null, new XmlAdapter(), new PlainTextAdapter(), null);

        conversation.addOrUpdateScenario(0, "ruby", "require 'java'\n$employees.employee[0].name == 'John';",
            "$employees.employee[0].name='John12345'; return 'Success'");

        ScenarioExecutionWrapper wrapper = new ScenarioExecutionWrapper(conversation);

        CamelContext context = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(context);
        Message message = new DefaultMessage();
        message.setBody(TestHelper.XML_DATA);
        exchange.setIn(message);

        String s = wrapper.process(exchange);

        Assert.assertTrue(s.contains("Success"));
    }


    @Test
    public void testReturnRubyEmptyHash() throws Exception
    {
        conversation = new ConversationImpl(2, "Conversation2", null, null, new XmlAdapter(), new XmlAdapter(), null);

        conversation.addOrUpdateScenario(0, "ruby", "require 'java'\n$employees.employee[0].name == 'John';",
            "$employees.employee[0].name='John12345';" +
                "return {}");

        CamelContext context = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(context);
        Message message = new DefaultMessage();
        message.setBody(TestHelper.XML_DATA);
        exchange.setIn(message);

        ScenarioExecutionWrapper wrapper = new ScenarioExecutionWrapper(conversation);
        String s = wrapper.process(exchange);
        Assert.assertTrue(s.contains("rubyhash"));
    }

    @Test
    public void testReturnRubyNotEmptyHash() throws Exception
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put(XmlAdapter.PARAM_ROOT_TAG_NAME, "root");
        Adapter outAdapter = new XmlAdapter(params);

        conversation = new ConversationImpl(2, "Conversation2", null, null, new XmlAdapter(), outAdapter, null);

        conversation.addOrUpdateScenario(0, "ruby", "require 'java'\n$employees.employee[0].name == 'John';",
            "$employees.employee[0].name='John12345';" +
                "return { :nilProperty => nil," +
                ":numberProperty => 1234," +
                ":arrayProperty => [ ]" +
                "" +
                "" +
                "}");


        ScenarioExecutionWrapper wrapper = new ScenarioExecutionWrapper(conversation);

        CamelContext context = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(context);
        Message message = new DefaultMessage();
        message.setBody(TestHelper.XML_DATA);
        exchange.setIn(message);

        String s = wrapper.process(exchange);
        Assert.assertTrue(s.contains("root"));
    }

    @Test
    public void testReturnJavaScriptNativeObject() throws Exception
    {

        String criteria = "employees.employee[0].name=='John';";
        String execution = "employees.employee[0].name='John12345';\n" +
            "var xxx= { " +
            "stringFieldName: \"xxxx\", " +
            "numberFieldName: 1234, " +
            "arrayField:[" +
            "       [134, 12345]," +
            "       [1234, 123456]" +
            "]," +
            "objectProperty:{stringgg:'ffff'}," +
            "undefinedVar: undefined" +
            "}\n" +
            "xxx";

        conversation = new ConversationImpl(2, "Conversation2", null, null, new XmlAdapter(), new XmlAdapter(), null);

        conversation.addOrUpdateScenario(0, "javascript", criteria, execution);

        ScenarioExecutionWrapper wrapper = new ScenarioExecutionWrapper(conversation);

        CamelContext context = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(context);
        Message message = new DefaultMessage();
        message.setBody(TestHelper.XML_DATA);
        exchange.setIn(message);
        
        String s = wrapper.process(exchange);
        System.out.println("s = " + s);

        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new StringReader(s));

        Element rootElement = doc.getRootElement();
        Assert.assertEquals(rootElement.getName(), "nativeobject");

        List<Element> list = rootElement.getChildren();

        Assert.assertEquals("arrayField", list.get(0).getName());
        Assert.assertEquals("arrayField", list.get(1).getName());
        Assert.assertEquals("objectProperty", list.get(2).getName());
        Assert.assertEquals("undefinedVar", list.get(3).getName());
        Assert.assertEquals("numberFieldName", list.get(4).getName());
        Assert.assertEquals("stringFieldName", list.get(5).getName());
    }
}
