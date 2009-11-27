package com.tacitknowledge.simulator.camel;

import com.tacitknowledge.simulator.ConversationScenario;
import com.tacitknowledge.simulator.formats.XmlAdapter;
import com.tacitknowledge.simulator.impl.ConversationScenarioImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 24.11.2009
 * Time: 17:26:26
 *
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 */
public class ScenarioExecutionWrapperTest {
    private static final String TEST_DATA = "<employees>\n" +
            "    <employee>\n" +
            "        <name>John</name>\n" +
            "        <title>Manager</title>\n" +
            "    </employee>\n" +
            "    <employee>\n" +
            "        <name>Sara</name>\n" +
            "        <title>Clerk</title>\n" +
            "    </employee>\n" +
            "    <reportDate>2009-11-05</reportDate>\n" +
            "    <roles>\n" +
            "        <role>Clerk</role>\n" +
            "        <role>Manager</role>\n" +
            "        <role>Accountant</role>\n" +
            "    </roles>\n" +
            "</employees>";

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

        ConversationScenario scenario = createScenario(criteria, execution);


        List<ConversationScenario> scenarios = new ArrayList<ConversationScenario>();
        scenarios.add(scenario);
        ScenarioExecutionWrapper wrapper = new ScenarioExecutionWrapper(scenarios, new XmlAdapter(), new XmlAdapter());

        String s = wrapper.process(TEST_DATA);
        Assert.assertTrue(s.contains("John12345"));

        //modify the script and see what happens
        scenario.setScripts(criteria, "employees.employee[0].name='John1234544444444';" +
                "employees", "javascript");

        s = wrapper.process(TEST_DATA);

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

        scenarios.add(createScenario(criteria1, execution1));
        scenarios.add(createScenario(criteria2, execution2));
        scenarios.add(createScenario(criteria3, execution3));

        ScenarioExecutionWrapper wrapper = new ScenarioExecutionWrapper(scenarios, new XmlAdapter(), new XmlAdapter());

        String s = wrapper.process(TEST_DATA);
        Assert.assertFalse(s.contains("Johnffff"));
        Assert.assertFalse(s.contains("John12345"));
        Assert.assertTrue(s.contains("Johnaaaa"));



    }


    private ConversationScenario createScenario(String criteria, String execution) {
        ConversationScenario scenario = new ConversationScenarioImpl("javascript", criteria, execution);
        scenario.setActive(true);
        return scenario;
    }
}
