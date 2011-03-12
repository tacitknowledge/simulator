package com.tacitknowledge.simulator;

/**
 * Defines the interface for the implementations of the ConversationManager.
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public interface ConversationManager
{
    /**
     * (Re)Loads conversations and start them in camel
     * @param systemsDirectoryPath systems directory path
     * @throws Exception exception
     */
    void loadConversations(String systemsDirectoryPath) throws Exception;
    
    /**
     * Register all listeners specifying a location
     *
     * @param fileLocation Listener file location and name
     */
    void registerListeners(String fileLocation);
}
