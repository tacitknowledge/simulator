package com.tacitknowledge.simulator.configuration.loaders;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.tacitknowledge.simulator.Conversation;

public class ConversationsLoader
{
    private ConversationsLoader()
    {}

    /**
     * Iterates through systems and each system conversations and returns a list of parsed
     * conversations (which contains scenarios)
     * 
     * @param systemsPath - system path (usually /systems)
     * @return
     * @throws IOException
     */
    public static List<Conversation> loadConversations(String systemsPath) throws IOException
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
                Conversation conversation = ConversationLoader.parseConversationFromPath(systemDir
                        .getAbsolutePath());
                conversations.add(conversation);
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
