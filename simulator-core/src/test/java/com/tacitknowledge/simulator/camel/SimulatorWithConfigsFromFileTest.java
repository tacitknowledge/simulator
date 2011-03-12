package com.tacitknowledge.simulator.camel;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.tacitknowledge.simulator.BaseConfigurable;
import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.RouteManager;
import com.tacitknowledge.simulator.configuration.loaders.ConversationLoader;
import com.tacitknowledge.simulator.configuration.loaders.ConversationsLoader;
import com.tacitknowledge.simulator.configuration.loaders.ScenarioLoader;
import com.tacitknowledge.simulator.transports.FileTransport;
import com.tacitknowledge.simulator.utils.Configuration;

public class SimulatorWithConfigsFromFileTest
{
    private ScenarioLoader scenarioLoader;

    private ConversationLoader conversationLoader;

    private ConversationsLoader conversationsLoader;

    @Before
    public void setUp()
    {
        scenarioLoader = new ScenarioLoader();
        conversationLoader = new ConversationLoader(scenarioLoader);
        conversationsLoader = new ConversationsLoader(conversationLoader);
    }

    @Test
    public void startSimulator() throws Exception
    {
        Resource resource = new ClassPathResource(
                Configuration.getPropertyAsString(Configuration.SYSTEMS_DIRECTORY_NAME));
        Map<String, Conversation> conversations = conversationsLoader.loadConversations(resource
                .getFile().getAbsolutePath());

        RouteManager routeManager = new RouteManagerImpl();
        routeManager.start();

        for (Entry<String, Conversation> entry : conversations.entrySet())
        {
            Conversation conversation = entry.getValue();

            // TODO: refactor this
            // Workaround: relative paths don't work from junit and camel :) Don't know why yet.
            workaroundAbsolutePaths((BaseConfigurable) conversation.getInboundTransport());
            workaroundAbsolutePaths((BaseConfigurable) conversation.getOutboundTransport());

            routeManager.activate(conversation);
        }

        // Camel executes routes in separate threads. Let's wait a little ...
        Thread.sleep(3000);

        File resultFile = new File(getTestsDirectory() + "/output/output.xml");
        assertTrue(resultFile.exists());
    }

    private void workaroundAbsolutePaths(BaseConfigurable transport) throws IOException
    {
        if (transport instanceof FileTransport)
        {
            String dir = transport.getParamValue("directoryName");
            transport.setParamValue("directoryName", getTestsDirectory() + "/" + dir);
        }
    }

    private String getTestsDirectory() throws IOException
    {
        return new ClassPathResource(".").getFile().getAbsolutePath();
    }
}
