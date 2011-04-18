package com.tacitknowledge.simulator.configuration.loaders;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.tacitknowledge.simulator.ScenarioParsingException;
import com.tacitknowledge.simulator.impl.ScenarioFactory;
import com.tacitknowledge.simulator.impl.ScenarioImpl;

public class ScenarioLoaderTest
{
    private ScenarioFactory scenarioFactory = new ScenarioFactory();
    
    private ScenarioLoader scenarioLoader = new ScenarioLoader(scenarioFactory);

    @Test
    public void loadScenario() throws IOException, ScenarioParsingException
    {
        Resource resource = new ClassPathResource("systems/sys1/conv1/scenario1.scn");

        ScenarioImpl scenario = (ScenarioImpl) scenarioLoader
                .parseScenarioFromFile(resource.getFile().getAbsolutePath());

        assertTrue(scenario.getCriteriaScript().startsWith("1==1"));
        assertTrue(scenario.getTransformationScript().startsWith("users."));
    }

    @Test
    public void badFormatScenario() throws IOException
    {
        Resource resource = new ClassPathResource("systems/sys1/conv1/scenarioBadFormat.scn");
        String scenarioFileName = resource.getFile().getAbsolutePath();

        try
        {
            scenarioLoader.parseScenarioFromFile(scenarioFileName);

            fail("parsing of scenario scenarioBadFormat.scn must fail");
        }
        catch (ScenarioParsingException ex)
        {
            // Exception message MUST show which scenario has failed
            assertTrue(ex.getMessage().contains(scenarioFileName));
        }
    }
}
