package com.tacitknowledge.simulator.configuration.loaders;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.ConversationScenario;
import com.tacitknowledge.simulator.impl.ConversationFactory;
import com.tacitknowledge.simulator.impl.ConversationScenarioFactory;

public class ConversationsLoaderTest
{

    @Test
    public void loadAllConversations() throws IOException
    {
        Resource resource = new ClassPathResource("systems");

        ConversationScenarioFactory scenarioFactory = new ConversationScenarioFactory();
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
            Collection<ConversationScenario> scenarios = conversation.getScenarios().values();
            assertNotNull(scenarios);

            for (ConversationScenario scenario : scenarios)
            {
                assertFalse(scenario.getCriteriaScript().isEmpty());
                assertFalse(scenario.getTransformationScript().isEmpty());
            }
        }
    }
}
