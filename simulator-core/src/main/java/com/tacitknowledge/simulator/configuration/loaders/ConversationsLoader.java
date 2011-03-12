package com.tacitknowledge.simulator.configuration.loaders;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.tacitknowledge.simulator.Conversation;

public class ConversationsLoader
{
    /**
     * Logger for the ConversationsLoader class.
     */
    private static Logger logger = LoggerFactory.getLogger(ConversationsLoader.class);

    private ConversationLoader conversationLoader;

    public ConversationsLoader(ConversationLoader conversationLoader)
    {
        this.conversationLoader = conversationLoader;
    }

    /**
     * Iterates through systems and each system conversations and returns a list of parsed
     * conversations (which contains scenarios)
     * 
     * @param systemsPath - system path (usually /systems)
     * @return a map with (path,conversation) 
     * @throws IOException
     */
    public Map<String, Conversation> loadConversations(String systemsPath) throws IOException
    {
        Map<String, Conversation> conversations = new HashMap<String, Conversation>();

        Resource resource = new FileSystemResource(systemsPath);

        File[] systemDirs = resource.getFile().listFiles(new DirectoryFilter());

        // iterate through each system
        for (File systemDir : systemDirs)
        {
            // find and iterate through each conversation directory
            File[] conversationDirs = systemDir.listFiles(new DirectoryFilter());

            for (File conversationDir : conversationDirs)
            {
                try
                {
                    String path = conversationDir.getAbsolutePath();
                    Conversation conversation = conversationLoader.parseConversationFromPath(path);
                    if (conversation != null)
                    {
                        conversations.put(path, conversation);
                    }
                }
                catch (IOException ex)
                {
                    String msg = String.format("Could not parse conversation from '%s' directory",
                            conversationDir.getAbsolutePath());
                    logger.warn(msg, ex);
                }
            }
        }

        return conversations;
    }

    private static class DirectoryFilter implements FileFilter
    {

        public boolean accept(File pathname)
        {
            return pathname.isDirectory();
        }

    }
}
