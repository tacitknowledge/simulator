package com.tacitknowledge.simulator.camel;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.ConversationScenario;
import com.tacitknowledge.simulator.TestHelper;
import com.tacitknowledge.simulator.formats.JsonAdapter;
import com.tacitknowledge.simulator.formats.PlainTextAdapter;
import com.tacitknowledge.simulator.formats.XmlAdapter;
import com.tacitknowledge.simulator.impl.ConversationScenarioImpl;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: 24.11.2009
 * Time: 17:26:26
 *
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 */
public class ScenarioExecutionWrapperTest {

    @Test
    public void testWithoutScenarios() throws Exception {
        ArrayList<ConversationScenario> list = new ArrayList<ConversationScenario>();
        list.add(new ConversationScenarioImpl(1,"javascript","true","text"));
        ScenarioExecutionWrapper wrapper = new ScenarioExecutionWrapper(list, new PlainTextAdapter(), new PlainTextAdapter());

        String testString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><xxxxxx/>";
        String s = wrapper.process(testString);
        Assert.assertNotNull(s);
        Assert.assertSame(testString, s);
    }

    @Test
    public void testWithOneScenario() throws Exception {

        String criteria = "employees.employee[0].name=='John';";
        String execution = "employees.employee[0].name='John12345';" +
                "employees";

        ConversationScenario scenario = createScenario(0, criteria, execution, "javascript");


        List<ConversationScenario> scenarios = new ArrayList<ConversationScenario>();
        scenarios.add(scenario);
        ScenarioExecutionWrapper wrapper = new ScenarioExecutionWrapper(scenarios, new XmlAdapter(), new XmlAdapter());

        String s = wrapper.process(TestHelper.XML_DATA);
        Assert.assertTrue(s.contains("John12345"));

        //modify the script and see what happens
        scenario.setScripts(criteria, "employees.employee[0].name='John1234544444444';" +
                "employees", "javascript");

        s = wrapper.process(TestHelper.XML_DATA);

        Assert.assertTrue(s.contains("John1234544444444"));
    }

    @Test
    public void testTreeScenarios() throws Exception {

        List<ConversationScenario> scenarios = new ArrayList<ConversationScenario>();

        String criteria1 = "employees.employee[0].name=='John12345';"; //false
        String execution1 = "employees.employee[0].name='Johnffff';employees";

        String criteria2 = "employees.employee[0].name=='John';";      //true
        String execution2 = "employees.employee[0].name='Johnaaaa';employees";  //this script should be executed

        String criteria3 = "employees.employee[0].name=='Johnffff';";      //false
        String execution3 = "employees.employee[0].name='John12345';employees";

        scenarios.add(createScenario(0, criteria1, execution1, "javascript"));
        scenarios.add(createScenario(0, criteria2, execution2, "javascript"));
        scenarios.add(createScenario(0, criteria3, execution3, "javascript"));

        ScenarioExecutionWrapper wrapper = new ScenarioExecutionWrapper(scenarios, new XmlAdapter(), new XmlAdapter());

        String s = wrapper.process(TestHelper.XML_DATA);
        Assert.assertFalse(s.contains("Johnffff"));
        Assert.assertFalse(s.contains("John12345"));
        Assert.assertTrue(s.contains("Johnaaaa"));

    }


    @Test
    public void testDifferentAdapters() throws Exception {

        List<ConversationScenario> scenarios = new ArrayList<ConversationScenario>();

        String criteria2 = "employees.employee[0].name=='John';";      //true
        String execution2 = "employees.employee[0].name='Johnaaaa';employees";  //this script should be executed

        scenarios.add(createScenario(0, criteria2, execution2, "javascript"));

        JsonAdapter outAdapter = new JsonAdapter();
        Map<String, String> param = new HashMap<String, String>();
        param.put(JsonAdapter.PARAM_JSON_CONTENT, "employees");
        outAdapter.setParameters(param);

        ScenarioExecutionWrapper wrapper = new ScenarioExecutionWrapper(scenarios, new XmlAdapter(), outAdapter);

        String s = wrapper.process(TestHelper.XML_DATA);
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
    public void testRubyScenario() throws Exception {

        ScenarioExecutionWrapper wrapper = createExecutionWrapper(
                "require 'java'\n$employees.employee[0].name == 'John';",
                "$employees.employee[0].name='John12345';" +
                         "$employees", new XmlAdapter(), new XmlAdapter());

        String s = wrapper.process(TestHelper.XML_DATA);
        Assert.assertTrue(s.contains("John12345"));
    }

     @Test
    public void testReturnDifferentObject() throws Exception {

        ScenarioExecutionWrapper wrapper = createExecutionWrapper("require 'java'\n$employees.employee[0].name == 'John';",
                 "$employees.employee[0].name='John12345';" +
                         "return 'Success'", new XmlAdapter(), new PlainTextAdapter());
        String s = wrapper.process(TestHelper.XML_DATA);
        Assert.assertTrue(s.contains("Success"));
    }


    @Test
    public void testReturnRubyEmptyHash() throws Exception
    {

        ScenarioExecutionWrapper wrapper = createExecutionWrapper("require 'java'\n$employees.employee[0].name == 'John';",
                "$employees.employee[0].name='John12345';" +
                        "return {}", new XmlAdapter(), new XmlAdapter());
        String s = wrapper.process(TestHelper.XML_DATA);
        Assert.assertTrue(s.contains("rubyhash"));
    }

    @Test
    public void testReturnRubyNotEmptyHash() throws Exception
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put(XmlAdapter.PARAM_ROOT_TAG_NAME, "root");
        Adapter outAdapter = new XmlAdapter(params);

        ScenarioExecutionWrapper wrapper = createExecutionWrapper("require 'java'\n$employees.employee[0].name == 'John';",
                "$employees.employee[0].name='John12345';" +
                        "return { :nilProperty => nil," +
                        ":numberProperty => 1234," +
                        ":arrayProperty => [ ]" +
                        "" +
                        "" +
                        "}", new XmlAdapter(), outAdapter);
        String s = wrapper.process(TestHelper.XML_DATA);
        Assert.assertTrue(s.contains("root"));
    }

    private ScenarioExecutionWrapper createExecutionWrapper(String criteria, String execution, Adapter inAdapter, Adapter outAdapter) {
        ConversationScenario scenario = createScenario(0, criteria, execution, "ruby");
        List<ConversationScenario> scenarios = new ArrayList<ConversationScenario>();
        scenarios.add(scenario);
        return new ScenarioExecutionWrapper(scenarios, inAdapter, outAdapter);
    }

    private ConversationScenario createScenario(int scenarioId, String criteria, String execution, String language) {
        ConversationScenario scenario = new ConversationScenarioImpl(scenarioId, language, criteria, execution);
        scenario.setActive(true);
        return scenario;
    }




     @Test
    public void testReturnJavaScriptNativeObject() throws Exception {

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

        ConversationScenario scenario = createScenario(0, criteria, execution, "javascript");


        List<ConversationScenario> scenarios = new ArrayList<ConversationScenario>();
        scenarios.add(scenario);
        ScenarioExecutionWrapper wrapper = new ScenarioExecutionWrapper(scenarios, new XmlAdapter(), new XmlAdapter());

         String s = wrapper.process(TestHelper.XML_DATA);
         System.out.println("s = " + s);
         
         SAXBuilder builder = new SAXBuilder();
         Document doc = builder.build(new StringReader(s));
         
         Element rootElement = doc.getRootElement();
         Assert.assertEquals(rootElement.getName(),"nativeobject");

         List<Element> list = rootElement.getChildren();

         Assert.assertEquals("arrayField", list.get(0).getName());
         Assert.assertEquals("arrayField", list.get(1).getName());
         Assert.assertEquals("objectProperty", list.get(2).getName());
         Assert.assertEquals("undefinedVar", list.get(3).getName());
         Assert.assertEquals("numberFieldName", list.get(4).getName());
         Assert.assertEquals("stringFieldName", list.get(5).getName());
     }
}
