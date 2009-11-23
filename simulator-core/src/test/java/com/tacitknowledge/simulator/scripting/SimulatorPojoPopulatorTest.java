package com.tacitknowledge.simulator.scripting;

import com.tacitknowledge.simulator.*;
import com.tacitknowledge.simulator.formats.AdapterFactory;
import com.tacitknowledge.simulator.formats.FormatConstants;
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
            // --- Now, get back a SimulatorPojo from the beans map
            SimulatorPojo pojo =
                    SimulatorPojoPopulator.getInstance().populateSimulatorPojoFromBean(beansMap);

            assertTrue(
                    "SimulatorPojo's root from SimulatorPojoPopulator " +
                            "should be equal to original pojo's root",
                    originalPojo.getRoot().equals(pojo.getRoot()));
        }
        catch (SimulatorException e)
        {
            fail("Shouldn't get an exception here");
        }
    }

    public void testSimulatorPojoOriginallyFromXml()
    {
        // --- Get the SimulatorPojo from an XML input
        Adapter adapter = AdapterFactory.getAdapter(FormatConstants.XML);

        SimulatorPojo originalPojo = null;
        try
        {
            originalPojo = adapter.adaptFrom(TestHelper.XML_DATA);
        }
        catch (FormatAdapterException fae)
        {
            fail("Shouldn't be getting an exception here");
        }

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
            // --- Now, get back a SimulatorPojo from the beans map
            SimulatorPojo pojo =
                    SimulatorPojoPopulator.getInstance().populateSimulatorPojoFromBean(beansMap);

            assertTrue(
                    "SimulatorPojo's root from SimulatorPojoPopulator " +
                            "should be equal to original pojo's root",
                    originalPojo.getRoot().equals(pojo.getRoot()));
        }
        catch (SimulatorException e)
        {
            fail("Shouldn't get an exception here");
        }
    }
}
