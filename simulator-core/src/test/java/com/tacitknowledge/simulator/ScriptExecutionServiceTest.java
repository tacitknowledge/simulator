package com.tacitknowledge.simulator;

import com.tacitknowledge.simulator.formats.AdapterFactory;
import com.tacitknowledge.simulator.formats.FormatConstants;
import com.tacitknowledge.simulator.formats.XmlAdapter;
import com.tacitknowledge.simulator.scripting.ScriptException;
import com.tacitknowledge.simulator.scripting.ScriptExecutionService;
import javassist.ClassPool;
import junit.framework.TestCase;

import java.util.Map;

/**
 * This test will help us to validate we are able to read and execute the different scripting
 * languages
 *
 * @author JOrge Galindo (jgalindo@tacitknowledge.com)
 */
public class ScriptExecutionServiceTest extends TestCase
{
    /**
     * This method will test the evaluation for JavaScript
     *
     * @throws Exception if there is a problem evaluating the script
     */
    public void testEvalJavaScript() throws Exception
    {
        // --- Default Javassist' JVM class pool
        ClassPool pool = ClassPool.getDefault();

        // --- First, get the SimulatorPojo from the data
        XmlAdapter adapter = (XmlAdapter) AdapterFactory.getAdapter(FormatConstants.XML);
        // --- Get the data in a strcutured form as a SimulatorPojo
        Map<String, Object> beans = adapter.generateBeans(TestHelper.XML_DATA);

        // --- Now, get a ScriptExecutionService and set the language (javascript)
        ScriptExecutionService execServ = new ScriptExecutionService();
        execServ.setLanguage("javascript");

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
    public void testEvalRuby() throws ScriptException
    {
        String myScript = "puts 'Hello Ruby'\n return 'hello'";
        ScriptExecutionService execServ = new ScriptExecutionService();
        execServ.setLanguage("ruby");
        Object result = execServ.eval(myScript, "myScript.rb", null);
        assertEquals("hello", result.toString());
    }

}
