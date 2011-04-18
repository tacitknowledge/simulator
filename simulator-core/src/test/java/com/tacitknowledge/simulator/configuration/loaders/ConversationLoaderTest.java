package com.tacitknowledge.simulator.configuration.loaders;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.Scenario;
import com.tacitknowledge.simulator.impl.ConversationFactory;
import com.tacitknowledge.simulator.impl.ScenarioFactory;

public class ConversationLoaderTest
{
    @Test
    public void loadScenarioListTest() throws IOException
    {
        Resource resource = new ClassPathResource("systems/sys1/conv1");
        
        ScenarioFactory scenarioFactory = new ScenarioFactory();
        ConversationFactory conversationFactory = new ConversationFactory(scenarioFactory);
        ScenarioLoader scenarioLoader = new ScenarioLoader(scenarioFactory);
        ConversationLoader conversationLoader = new ConversationLoader(conversationFactory, scenarioLoader);

        Conversation conversation = conversationLoader.loadSingleConversationInDirectory(
            resource.getFile().getAbsolutePath());

        Collection<Scenario> scenarios = conversation.getScenarios().values();

        assertNotNull(scenarios);
        assertTrue(scenarios.size() == 3);

        for (Scenario scen : scenarios)
        {
            assertNotNull(scen.getCriteriaScript());
            assertNotNull(scen.getTransformationScript());

            assertFalse(scen.getCriteriaScript().isEmpty());
            assertFalse(scen.getTransformationScript().isEmpty());
        }
    }
    
    @Test
    public void loadAllConversations() throws IOException
    {
        Resource resource = new ClassPathResource("systems");

        ScenarioFactory scenarioFactory = new ScenarioFactory();
        ConversationFactory conversationFactory = new ConversationFactory(scenarioFactory);
        ScenarioLoader scenarioLoader = new ScenarioLoader(scenarioFactory);
        ConversationLoader conversationLoader = new ConversationLoader(conversationFactory, scenarioLoader);

        Map<String, Conversation> conversations = conversationLoader.loadAllConversationsInDirectory(
            resource.getFile().getAbsolutePath());

        assertNotNull(conversations);

        assertFalse(conversations.isEmpty());

        for (Entry<String, Conversation> entry : conversations.entrySet())
        {
            Conversation conversation = entry.getValue();
            Collection<Scenario> scenarios = conversation.getScenarios().values();
            assertNotNull(scenarios);

            for (Scenario scenario : scenarios)
            {
                assertFalse(scenario.getCriteriaScript().isEmpty());
                assertFalse(scenario.getTransformationScript().isEmpty());
            }
        }
    }
}
