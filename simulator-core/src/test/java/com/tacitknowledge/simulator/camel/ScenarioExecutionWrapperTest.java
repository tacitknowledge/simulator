package com.tacitknowledge.simulator.camel;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.DefaultMessage;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.Configurable;
import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.TestHelper;
import com.tacitknowledge.simulator.formats.JsonAdapter;
import com.tacitknowledge.simulator.formats.PlainTextAdapter;
import com.tacitknowledge.simulator.formats.XmlAdapter;
import com.tacitknowledge.simulator.impl.ConversationImpl;

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
        conversation = new ConversationImpl("Conversation1", null, null, new PlainTextAdapter(), new PlainTextAdapter());
        conversation.addScenario("file.scn", "javascript", "true", "text");

        ScenarioExecutionWrapper wrapper = new ScenarioExecutionWrapper(conversation);

        String testString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><xxxxxx/>";
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        Message message = new DefaultMessage();
        message.setBody(testString);
        exchange.setIn(message);
        wrapper.process(exchange);
        String s = exchange.getOut().getBody(String.class);
        Assert.assertNotNull(s);
        Assert.assertSame(testString, s);
    }

    @Test
    public void testWithOneScenario() throws Exception
    {

        String criteria = "employees.employee[0].name=='John';";
        String execution = "employees.employee[0].name='John12345';employees";

        conversation = new ConversationImpl("Conversation1", null, null, new XmlAdapter(), new XmlAdapter());
        conversation.addScenario("file.scn", "javascript", criteria, execution);

        ScenarioExecutionWrapper wrapper = new ScenarioExecutionWrapper(conversation);

        CamelContext context = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(context);
        Message message = new DefaultMessage();
        message.setBody(TestHelper.XML_DATA);
        exchange.setIn(message);
        wrapper.process(exchange);
        String s = exchange.getOut().getBody(String.class);
        Assert.assertTrue(s.contains("John12345"));
    }

    @Test
    public void testTreeScenarios() throws Exception
    {
        conversation = new ConversationImpl("Conversation2", null, null, new XmlAdapter(), new XmlAdapter());

        String criteria1 = "employees.employee[0].name=='John12345';"; //false
        String execution1 = "employees.employee[0].name='Johnffff';employees";

        String criteria2 = "employees.employee[0].name=='John';";      //true
        String execution2 = "employees.employee[0].name='Johnaaaa';employees";  //this script should be executed

        String criteria3 = "employees.employee[0].name=='Johnffff';";      //false
        String execution3 = "employees.employee[0].name='John12345';employees";

        conversation.addScenario("file1.scn", "javascript", criteria1, execution1);
        conversation.addScenario("file2.scn", "javascript", criteria2, execution2);
        conversation.addScenario("file3.scn", "javascript", criteria3, execution3);

        ScenarioExecutionWrapper wrapper = new ScenarioExecutionWrapper(conversation);

        CamelContext context = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(context);
        Message message = new DefaultMessage();
        message.setBody(TestHelper.XML_DATA);
        exchange.setIn(message);

        wrapper.process(exchange);
        String s = exchange.getOut().getBody(String.class);
        Assert.assertFalse(s.contains("Johnffff"));
        Assert.assertFalse(s.contains("John12345"));
        Assert.assertTrue(s.contains("Johnaaaa"));

    }


    @Test
    public void testDifferentAdapters() throws Exception
    {


        String criteria2 = "employees.employee[0].name=='John';";      //true
        String execution2 = "employees.employee[0].name='Johnaaaa';employees";  //this script should be executed

        Map<String, String> param = new HashMap<String, String>();
        param.put(JsonAdapter.PARAM_JSON_CONTENT, "employees");
        JsonAdapter outAdapter = new JsonAdapter(Configurable.BOUND_OUT, param);

        conversation = new ConversationImpl("Conversation2", null, null, new XmlAdapter(), outAdapter);

        conversation.addScenario("file.scn", "javascript", criteria2, execution2);

        ScenarioExecutionWrapper wrapper = new ScenarioExecutionWrapper(conversation);

        CamelContext context = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(context);
        Message message = new DefaultMessage();
        message.setBody(TestHelper.XML_DATA);
        exchange.setIn(message);

        wrapper.process(exchange);
        String s = exchange.getOut().getBody(String.class);
        Assert.assertFalse(s.contains("Johnffff"));
        Assert.assertFalse(s.contains("John12345"));
        Assert.assertTrue(s.contains("Johnaaaa"));

        // --- Test that the returned String is a valid JSON format
        try
        {
            new JSONObject(s);
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

        conversation = new ConversationImpl("Conversation2", null, null, new XmlAdapter(), new XmlAdapter());

        conversation.addScenario("file.scn", "ruby", "require 'java'\n$employees.employee[0].name == 'John';", "$employees.employee[0].name='John12345';" +
                "$employees" );

        ScenarioExecutionWrapper wrapper = new ScenarioExecutionWrapper(conversation);

        CamelContext context = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(context);
        Message message = new DefaultMessage();
        message.setBody(TestHelper.XML_DATA);
        exchange.setIn(message);

        wrapper.process(exchange);
        String s = exchange.getOut().getBody(String.class);
        Assert.assertTrue(s.contains("John12345"));
    }

    @Test
    public void testReturnDifferentObject() throws Exception
    {
        conversation = new ConversationImpl("Conversation2", null, null, new XmlAdapter(), new PlainTextAdapter());

        conversation.addScenario("file.scn", "ruby", "require 'java'\n$employees.employee[0].name == 'John';",
            "$employees.employee[0].name='John12345'; return 'Success'");

        ScenarioExecutionWrapper wrapper = new ScenarioExecutionWrapper(conversation);

        CamelContext context = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(context);
        Message message = new DefaultMessage();
        message.setBody(TestHelper.XML_DATA);
        exchange.setIn(message);

        wrapper.process(exchange);
        String s = exchange.getOut().getBody(String.class);

        Assert.assertTrue(s.contains("Success"));
    }


    @Test
    public void testReturnRubyEmptyHash() throws Exception
    {
        conversation = new ConversationImpl("Conversation2", null, null, new XmlAdapter(), new XmlAdapter());

        conversation.addScenario("file.scn", "ruby", "require 'java'\n$employees.employee[0].name == 'John';",
            "$employees.employee[0].name='John12345';" +
                "return {}");

        CamelContext context = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(context);
        Message message = new DefaultMessage();
        message.setBody(TestHelper.XML_DATA);
        exchange.setIn(message);

        ScenarioExecutionWrapper wrapper = new ScenarioExecutionWrapper(conversation);
        wrapper.process(exchange);
        String s = exchange.getOut().getBody(String.class);
        Assert.assertTrue(s.contains("rubyhash"));
    }

    @Test
    public void testReturnRubyNotEmptyHash() throws Exception
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put(XmlAdapter.PARAM_ROOT_TAG_NAME, "root");
        Adapter outAdapter = new XmlAdapter(Configurable.BOUND_OUT, params);

        conversation = new ConversationImpl("Conversation2", null, null, new XmlAdapter(), outAdapter);

        conversation.addScenario("file.scn", "ruby", "require 'java'\n$employees.employee[0].name == 'John';",
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

        wrapper.process(exchange);
        String s = exchange.getOut().getBody(String.class);
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

        conversation = new ConversationImpl("Conversation2", null, null, new XmlAdapter(), new XmlAdapter());

        conversation.addScenario("file.scn", "javascript", criteria, execution);

        ScenarioExecutionWrapper wrapper = new ScenarioExecutionWrapper(conversation);

        CamelContext context = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(context);
        Message message = new DefaultMessage();
        message.setBody(TestHelper.XML_DATA);
        exchange.setIn(message);
        
        wrapper.process(exchange);
        String s = exchange.getOut().getBody(String.class);
        System.out.println("s = " + s);

        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new StringReader(s));

        Element rootElement = doc.getRootElement();
        Assert.assertEquals(rootElement.getName(), "nativeobject");

        @SuppressWarnings("unchecked")
        List<Element> list = rootElement.getChildren();

        List<String> fieldList = new ArrayList<String>();

        for(Element ele : list){
            fieldList.add(ele.getName());
        }

        Assert.assertTrue(fieldList.contains("arrayField"));
        Assert.assertTrue(fieldList.contains("arrayField"));
        Assert.assertTrue(fieldList.contains("objectProperty"));
        Assert.assertTrue(fieldList.contains("undefinedVar"));
        Assert.assertTrue(fieldList.contains("numberFieldName"));
        Assert.assertTrue(fieldList.contains("stringFieldName"));
    }
}
