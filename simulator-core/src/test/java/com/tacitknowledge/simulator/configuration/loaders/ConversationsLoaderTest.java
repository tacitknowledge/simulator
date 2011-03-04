package com.tacitknowledge.simulator.configuration.loaders;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

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

        List<Conversation> conversations = ConversationsLoader.loadConversations(resource.getFile()
                .getAbsolutePath());

        assertNotNull(conversations);

        assertFalse(conversations.isEmpty());

        for (Conversation conversation : conversations)
        {
            Collection<ConversationScenario> scenarios = conversation.getScenarios();
            assertNotNull(scenarios);

            for (ConversationScenario scenario : scenarios)
            {
                assertFalse(scenario.getCriteriaScript().isEmpty());
                assertFalse(scenario.getTransformationScript().isEmpty());
            }
        }
    }
}
