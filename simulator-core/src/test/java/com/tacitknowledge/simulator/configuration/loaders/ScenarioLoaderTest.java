package com.tacitknowledge.simulator.configuration.loaders;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.tacitknowledge.simulator.impl.ConversationScenarioImpl;

public class ScenarioLoaderTest
{
    @Test
    public void loadScenario() throws IOException
    {
        Resource resource = new ClassPathResource("systems/sys1/conv1/scenario1.scn");

        ConversationScenarioImpl scenario = (ConversationScenarioImpl) ScenarioLoader
                .parseScenarioFromFile(resource.getFile().getAbsolutePath());

        assertTrue(scenario.getCriteriaScript().startsWith("1==1"));
        assertTrue(scenario.getTransformationScript().startsWith("obj.response.body="));
    }
}
