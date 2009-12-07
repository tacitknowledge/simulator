package com.tacitknowledge.simulator.camel;

import com.tacitknowledge.simulator.ConversationScenario;
import com.tacitknowledge.simulator.TestHelper;
import com.tacitknowledge.simulator.formats.JsonAdapter;
import com.tacitknowledge.simulator.formats.XmlAdapter;
import com.tacitknowledge.simulator.impl.ConversationScenarioImpl;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

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
        ScenarioExecutionWrapper wrapper = new ScenarioExecutionWrapper(new ArrayList<ConversationScenario>(), new XmlAdapter(), new XmlAdapter());

        String testString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><xxxxxx/>";
        String s = wrapper.process(testString);
        Assert.assertEquals(testString, s);
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

//        String criteria1 = "employees.employee[0].name=='John12345';"; //false
//        String execution1 = "employees.employee[0].name='Johnffff';employees";

        String criteria2 = "employees.employee[0].name=='John';";      //true
        String execution2 = "employees.employee[0].name='Johnaaaa';employees";  //this script should be executed

//        String criteria3 = "employees.employee[0].name=='Johnffff';";      //false
//        String execution3 = "employees.employee[0].name='John12345';employees";

//        scenarios.add(createScenario(criteria1, execution1));
        scenarios.add(createScenario(0, criteria2, execution2, "javascript"));
//        scenarios.add(createScenario(criteria3, execution3));

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
    
    private ConversationScenario createScenario(int scenarioId, String criteria, String execution, String language) {
        ConversationScenario scenario = new ConversationScenarioImpl(scenarioId, language, criteria, execution);
        scenario.setActive(true);
        return scenario;
    }




     @Test
    public void testRubyScenario() throws Exception {

        String criteria = "" +
                "require 'java'\n" +
                "$employees.employee[0].name == 'John';";


        String execution = "$employees.employee[0].name='John12345';" +
                "$employees";

        ConversationScenario scenario = createScenario(0, criteria, execution, "ruby");


        List<ConversationScenario> scenarios = new ArrayList<ConversationScenario>();
        scenarios.add(scenario);
        ScenarioExecutionWrapper wrapper = new ScenarioExecutionWrapper(scenarios, new XmlAdapter(), new XmlAdapter());

        String s = wrapper.process(TestHelper.XML_DATA);
        Assert.assertTrue(s.contains("John12345"));
    }
}
