package com.tacitknowledge.simulator.configuration.loaders;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    
    private ConversationLoader conversationLoader = new ConversationLoader();
    
    /**
     * Iterates through systems and each system conversations and returns a list of parsed
     * conversations (which contains scenarios)
     * 
     * @param systemsPath - system path (usually /systems)
     * @return
     * @throws IOException
     */
    public List<Conversation> loadConversations(String systemsPath) throws IOException
    {
        List<Conversation> conversations = new ArrayList<Conversation>();

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
                    Conversation conversation = conversationLoader
                            .parseConversationFromPath(conversationDir.getAbsolutePath());
                    if (conversation != null)
                    {
                        conversations.add(conversation);
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
