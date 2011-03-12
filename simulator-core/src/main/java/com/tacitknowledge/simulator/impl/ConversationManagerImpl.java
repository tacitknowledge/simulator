package com.tacitknowledge.simulator.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.ConversationManager;
import com.tacitknowledge.simulator.ConversationScenario;
import com.tacitknowledge.simulator.RouteManager;
import com.tacitknowledge.simulator.configuration.EventDispatcher;
import com.tacitknowledge.simulator.configuration.SimulatorEventListener;
import com.tacitknowledge.simulator.configuration.loaders.ConversationsLoader;

/**
 * Conversation manager implementation.
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class ConversationManagerImpl implements ConversationManager
{
    /**
     * Logger for this class.
     */
    private static Logger logger = LoggerFactory.getLogger(ConversationManagerImpl.class);

    private RouteManager routeManager;

    private ConversationsLoader conversationsLoader;

    private Map<String, Conversation> activeConversations;

    public ConversationManagerImpl(RouteManager routeManager,
            ConversationsLoader conversationsLoader)
    {
        this.routeManager = routeManager;
        this.conversationsLoader = conversationsLoader;
    }

    /**
     * {@inheritDoc}
     */
    public void loadConversations(String systemsDirectoryPath) throws Exception
    {
        Map<String, Conversation> conversations = conversationsLoader
                .loadConversations(systemsDirectoryPath);

        // If this 1st call - just load/activate all conversations
        if (activeConversations == null || activeConversations.isEmpty())
        {
            logger.debug("1st call of conversations load. Enabling all of them ...");
            activateConversations(conversations.values());
            activeConversations = conversations;
            return;
        }

        boolean hasChanges = false;

        // Verify each existing active conversations if it's needed to reload
        for (Conversation activeConversation : activeConversations.values())
        {
            if (!conversations.containsKey(activeConversation.getId()))
            {
                // This active conversation doesn't exist anymore (was removed)
                // just delete it.
                logger.info(String.format(
                        "Conversation '%s' has been removed from configurations. Deactivating ...",
                        activeConversation.getId()));
                routeManager.delete(activeConversation);
                hasChanges = true;
                continue;
            }

            // Detect changes in conversation and reload if needed
            Conversation newConversation = conversations.get(activeConversation.getId());
            if (hasDifferencesInConfiguration(activeConversation, newConversation))
            {
                logger.info(String.format("Conversation '%s' has changes. Reloading ...",
                        activeConversation.getId()));
                routeManager.deactivate(activeConversation);
                routeManager.activate(newConversation);
                hasChanges = true;
            }
        }

        for (Conversation newConversation : conversations.values())
        {
            if (!activeConversations.containsKey(newConversation))
            {
                // This is a new conversation. Activate it!

                logger.info(String.format("Detected new conversation '%s'. Activating it ...",
                        newConversation.getId()));

                routeManager.activate(newConversation);
                hasChanges = true;
            }
        }

        if (hasChanges)
        {
            activeConversations = conversations;
        }
    }

    /**
     * Activate given conversations
     * @param conversations conversations to be activated
     * @throws Exception camael errors
     */
    private void activateConversations(Collection<Conversation> conversations) throws Exception
    {
        for (Conversation conversation : conversations)
        {
            routeManager.activate(conversation);
        }
    }

    /**
     * Compares conversations configuration. If there are any differences the method will return true,
     * otherwise it returns false
     * @param conv1 conversation
     * @param conv2 conversation
     * @return
     */
    private boolean hasDifferencesInConfiguration(Conversation conv1, Conversation conv2)
    {
        if (conv1.getIboundModifiedDate() != conv2.getIboundModifiedDate())
        {
            String msg = String.format(
                    "Found changes in inbound.properties of conversation '%s'. Reloading ...",
                    conv1.getId());
            logger.info(msg);
            return true;
        }

        if (conv1.getOutboundModifiedDate() != conv2.getOutboundModifiedDate())
        {
            String msg = String.format(
                    "Found changes in outbound.properties of conversation '%s'. Reloading ...",
                    conv1.getId());
            logger.info(msg);
            return true;
        }

        Map<String, ConversationScenario> conv1Scenarios = conv1.getScenarios();
        Map<String, ConversationScenario> conv2Scenarios = conv2.getScenarios();

        // If the number of scenario differs return true
        // Case when new scenario appeared or an scenario was removed
        if (conv1Scenarios.size() != conv2Scenarios.size())
        {
            String msg = String
                    .format("Found changes in scenarios of conversation '%s'. Reloading ...",
                            conv1.getId());
            logger.info(msg);
            return true;
        }

        // Compare last modified date of each scenario
        for (ConversationScenario scenario1 : conv1Scenarios.values())
        {
            if (!conv2Scenarios.containsKey(scenario1.getConfigurationFilePath()))
            {
                String msg = String.format(
                        "Found changes in scenarios of conversation '%s'. Reloading ...",
                        conv1.getId());
                logger.info(msg);
                return true;
            }

            ConversationScenario scenario2 = conv2Scenarios.get(scenario1
                    .getConfigurationFilePath());

            if (scenario1.getLastModifiedDate() != scenario2.getLastModifiedDate())
            {
                String msg = String.format(
                        "Found changes in scenario '%s' of conversation '%s'. Reloading ...",
                        scenario1.getConfigurationFilePath(), conv1.getId());
                logger.info(msg);
                return true;
            }
        }

        logger.debug(String.format("Conversation '%s' has no changes in configuration. Skipping.",
                conv1.getId()));

        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void registerListeners(final String filePath)
    {
        if (filePath != null)
        {
            File file = new File(filePath);
            if (file.exists() && file.canRead())
            {
                BufferedReader reader = null;
                try
                {
                    reader = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = reader.readLine()) != null)
                    {
                        line = line.trim();
                        if (line.length() > 0 && !line.startsWith("#"))
                        {
                            registerListenerImplementation(line);
                        }
                    }
                }
                catch (IOException ex)
                {
                    logger.info("Exception while trying to read listener file.", ex);
                }
                finally
                {
                    if (null != reader)
                    {
                        try
                        {
                            reader.close();
                        }
                        catch (IOException e)
                        {
                            logger.info("Unexpected IO exception: ", e);
                        }
                    }
                }
            }
            else
            {
                logger.info("Listeners not registered. Listener file not accesible.");
            }
        }
        else
        {
            logger.info("Listeners not registered. Listener file location is null.");
        }
    }

    /**
     * Register a SimulatorEventListener implementation in the EventDispatcher
     *
     * @param className - Qualified Class Name
     */
    @SuppressWarnings("rawtypes")
    private void registerListenerImplementation(final String className)
    {
        Class listenerClass;
        try
        {
            listenerClass = Class.forName(className);
            Object instanceObject = listenerClass.newInstance();
            EventDispatcher.getInstance().addSimulatorEventListener(
                    (SimulatorEventListener) instanceObject);
            logger.info("Registered class: {}", listenerClass);
        }
        catch (Exception e)
        {
            logger.info("Unable to register listener class: " + className + " due to:", e);
        }
    }
}
