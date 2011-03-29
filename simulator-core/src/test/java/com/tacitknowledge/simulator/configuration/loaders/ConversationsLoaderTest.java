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

public class ConversationsLoaderTest
{

    @Test
    public void loadAllConversations() throws IOException
    {
        Resource resource = new ClassPathResource("systems");

        ScenarioLoader scenarioLoader = new ScenarioLoader();
        ConversationLoader conversationLoader = new ConversationLoader(scenarioLoader);

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
