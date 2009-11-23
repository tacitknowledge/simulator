package com.tacitknowledge.simulator;

import junit.framework.TestCase;
import com.tacitknowledge.simulator.formats.AdapterFactory;
import com.tacitknowledge.simulator.formats.FormatConstants;
import com.tacitknowledge.simulator.scripting.ScriptExecutionService;
import com.tacitknowledge.simulator.scripting.PojoClassGenerator;

import java.util.Map;

import javassist.ClassPool;

/**
 * @author galo
 */
public class ScriptExecutionServiceTest extends TestCase
{
    public void testEval()
    {
        // --- Default Javassist' JVM class pool
        ClassPool pool = ClassPool.getDefault();
        
        // --- First, get the SimulatorPojo from the data
        Adapter adapter = AdapterFactory.getAdapter(FormatConstants.XML);

        try
        {
            // --- Get the data in a strcutured form as a SimulatorPojo
            SimulatorPojo pojo = adapter.adaptFrom(TestHelper.XML_DATA);

            // --- Get a map of (temporary and dinamically generated) beans out of the SimulatorPojo
            PojoClassGenerator generator = new PojoClassGenerator(pool);
            Map beans = generator.generateBeansMap(pojo);
            // --- Now, get a ScriptExecutionService and set the language (javascript)
            ScriptExecutionService execServ = new ScriptExecutionService();
            execServ.setLanguage("javascript");

            // --- Test a couple of values
            Object result = execServ.eval("employees", "Get employees", beans);
            assertNotNull(result);

            result = execServ.eval("employees.reportDate", "Is it today?", beans);
            assertEquals(result,"2009-11-05");

            result = execServ.eval("employees.employee[1].name", "Second employee name", beans);
            assertEquals(result,"Sara");

            // --- Detach the generated classes
            generator.detachGeneratedClasses();
            // --- Get rid of the reference to the generator and ClassPool
            generator = null;
            pool = null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

}
