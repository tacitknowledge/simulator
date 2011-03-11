package com.tacitknowledge.simulator;

/**
 * Defines the interface for the implementations of the ConversationManager.
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public interface ConversationManager
{
    /**
     * Register all listeners specifying a location
     *
     * @param fileLocation Listener file location and name
     */
    void registerListeners(String fileLocation);
}
