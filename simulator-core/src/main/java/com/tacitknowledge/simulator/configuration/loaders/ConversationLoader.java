package com.tacitknowledge.simulator.configuration.loaders;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.ConversationScenario;
import com.tacitknowledge.simulator.impl.ConversationImpl;

public class ConversationLoader
{
    private ConversationLoader()
    {}

    /**
     * Parse and constructs conversation from a given directory
     * 
     * @param conversationDir conversation directory (usually /systems/asystem/aconversation)
     * @return Conversation
     * @throws IOException
     */
    public static Conversation parseConversationFromPath(String conversationDir) throws IOException
    {
        Conversation conversation = new ConversationImpl();

        // TODO: parse inbound and outbound

        loadConversationScenarios(conversation, conversationDir);

        return conversation;
    }

    /**
     * Loads all conversation scenarios
     * 
     * @param conversation conversation
     * @param conversationDir conversation directory
     * @throws IOException
     */
    private static void loadConversationScenarios(Conversation conversation, String conversationDir)
            throws IOException
    {
        Resource resource = new FileSystemResource(conversationDir);

        File[] scenarioFiles = resource.getFile().listFiles(new FilenameFilter()
        {

            public boolean accept(File dir, String name)
            {
                return name.endsWith(".scn");
            }
        });

        // TODO: ID will be removed soon (artifact from MySQL)
        int scenarioId = 1;

        for (File scenarioFile : scenarioFiles)
        {
            ConversationScenario scenario = ScenarioLoader.parseScenarioFromFile(scenarioFile
                    .getAbsolutePath());

            conversation.addOrUpdateScenario(scenarioId, scenario.getScriptLanguage(),
                    scenario.getCriteriaScript(), scenario.getTransformationScript());

            scenarioId++;
        }
    }
}
