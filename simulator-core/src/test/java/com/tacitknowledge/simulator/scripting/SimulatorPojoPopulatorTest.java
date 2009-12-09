package com.tacitknowledge.simulator.scripting;

import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.TestHelper;
import javassist.ClassPool;
import junit.framework.TestCase;

import java.util.Map;

/**
 * @author galo
 */
public class SimulatorPojoPopulatorTest extends TestCase
{
    static ClassPool pool = new ClassPool(true);
    static PojoClassGenerator generator = new PojoClassGenerator(pool);

    public void testGetSimulatorPojoFromGeneratedBean()
    {
        // --- First, get the starting SimulatorPojo
        SimulatorPojo originalPojo = TestHelper.createOrderSimulatorPojo();

        // --- Now, get the BeansMap
        Map<String, Object> beansMap = null;
        try
        {
            beansMap = generator.generateBeansMap(originalPojo);
        }
        catch (Exception e)
        {
            fail("Shouldn't get an exception here");
        }

        try
        {
            // --- Now, get back a SimulatorPojo from the beans map' root entry
            SimulatorPojo pojo =
                    SimulatorPojoPopulatorImpl.getInstance().
                            populateSimulatorPojoFromBean(beansMap.get("order"));

            assertTrue(
                    "SimulatorPojo's root from SimulatorPojoPopulatorImpl " +
                            "should be equal to original pojo's root",
                    originalPojo.getRoot().equals(pojo.getRoot()));
        }
        catch (ObjectMapperException e)
        {
            e.printStackTrace();
            fail("Shouldn't get an exception here");

        }
    }
}
