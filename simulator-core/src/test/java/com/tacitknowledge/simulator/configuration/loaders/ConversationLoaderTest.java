package com.tacitknowledge.simulator.configuration.loaders;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.ConversationScenario;

public class ConversationLoaderTest
{
    @Test
    public void loadScenarioListTest() throws IOException
    {
        Resource resource = new ClassPathResource("systems/sys1/conv1");
        
        ScenarioLoader scenarioLoader = new ScenarioLoader();
        ConversationLoader conversationLoader = new ConversationLoader(scenarioLoader);

        Conversation conversation = conversationLoader.parseConversationFromPath(resource.getFile()
                .getAbsolutePath());

        Collection<ConversationScenario> scenarios = conversation.getScenarios().values();

        assertNotNull(scenarios);
        assertTrue(scenarios.size() == 3);

        for (ConversationScenario scen : scenarios)
        {
            assertNotNull(scen.getCriteriaScript());
            assertNotNull(scen.getTransformationScript());

            assertFalse(scen.getCriteriaScript().isEmpty());
            assertFalse(scen.getTransformationScript().isEmpty());
        }
    }
}
