package com.tacitknowledge.simulator.camel;

import com.tacitknowledge.simulator.ConversationScenario;
import com.tacitknowledge.simulator.formats.XmlAdapter;
import com.tacitknowledge.simulator.impl.ConversationScenarioImpl;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 24.11.2009
 * Time: 17:26:26
 *
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 */
public class ScenarioExecutionWrapperTest extends TestCase {
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

    public void testWithoutScenarios() throws Exception {
        ScenarioExecutionWrapper wrapper = new ScenarioExecutionWrapper(new ArrayList<ConversationScenario>(), new XmlAdapter(), new XmlAdapter());

        String testString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><xxxxxx/>";
        String s = wrapper.process(testString);
        assertEquals(testString, s);
    }

    public void testWithOneScenario() throws Exception {

        String criteria = "employees.employee[0].name=='John';";
        String execution = "employees.employee[0].name='John12345';" +
                "employees";

        ConversationScenario scenario = new ConversationScenarioImpl("javascript", criteria, execution);
        scenario.setActive(true);
        List<ConversationScenario> scenarios = new ArrayList<ConversationScenario>();
        scenarios.add(scenario);
        ScenarioExecutionWrapper wrapper = new ScenarioExecutionWrapper(scenarios, new XmlAdapter(), new XmlAdapter());

        String s = wrapper.process(TEST_DATA);
        assertTrue(s.contains("John12345"));

        //modify the script and see what happens
         scenario.setScripts(criteria,"employees.employee[0].name='John1234544444444';" +
                "employees","javascript");

        s = wrapper.process(TEST_DATA);
        
        assertTrue(s.contains("John1234544444444"));
    }

}
