package com.tacitknowledge.simulator.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.tacitknowledge.simulator.configuration.loaders.ConversationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.ConversationManager;
import com.tacitknowledge.simulator.Scenario;
import com.tacitknowledge.simulator.RouteManager;
import com.tacitknowledge.simulator.configuration.EventDispatcher;
import com.tacitknowledge.simulator.configuration.SimulatorEventListener;

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

    private ConversationLoader conversationLoader;

    private Map<String, Conversation> activeConversations;

    public ConversationManagerImpl(RouteManager routeManager,
            ConversationLoader conversationsLoader)
    {
        this.routeManager = routeManager;
        this.conversationLoader = conversationsLoader;
    }

    /**
     * {@inheritDoc}
     */
    public void loadConversations(String systemsDirectoryPath) throws Exception
    {
        //conversations variable is for comparing changed vs current instance level conversations
        Map<String, Conversation> conversations =
            conversationLoader.loadAllConversationsInDirectory(systemsDirectoryPath);

        if (noConversationsActivated())
        {
            logger.debug("1st call of conversations load. Enabling all of them ...");
            activateConversations(conversations.values());
        }
        else
        {
            checkAndDeleteConversationsThatDoNotExistAnymore(conversations);
            checkAndReactivateConversationsChanged(conversations);
            checkAndActivateNewConversations(conversations);
        }
        //now swap in new conversations for old
        activeConversations = conversations;

    }

    /**
     * Activate any new conversations that appeared since last reload.
     *
     * @param conversations all conversations found on FS at the moment
     * @return true if any new ones found
     * @throws Exception this is bad
     */
    private boolean checkAndActivateNewConversations(Map<String, Conversation> conversations)
        throws Exception
    {
        boolean newConversations = false;

        for (Conversation newConversation : conversations.values())
        {
            logger.debug("new conversation: " + newConversation.getId());

            // check if it is a new conversation.
            if (!activeConversations.containsKey(newConversation.getId()))
            {
                // This is a new conversation. Activate it!
                logger.info(String.format("Detected new conversation '%s'. Activating it ...",
                    newConversation.getId()));

                routeManager.activate(newConversation);
                newConversations = true;
            }
        }

        return newConversations;
    }

    /**
     * Delete any conversations that do not exist on the FS anymore
     *
     * @param conversations all conversations found on FS at the moment
     * @return true if any were deleted
     * @throws Exception this is bad
     */
    private boolean checkAndDeleteConversationsThatDoNotExistAnymore(
        Map<String, Conversation> conversations) throws Exception
    {
        boolean deletedAny = false;

        for (Conversation activeConversation : activeConversations.values())
        {
            // check if this conversation was deleted.
            if (!conversations.containsKey(activeConversation.getId()))
            {
                // This active conversation doesn't exist anymore (was removed)
                // just delete it.
                logger.info(String.format(
                    "Conversation '%s' has been removed from configurations. Deactivating ...",
                    activeConversation.getId()));

                routeManager.delete(activeConversation);
                deletedAny = true;
            }
        }

        return deletedAny;
    }

    /**
     * Reactivate any conversations that have been updated
     *
     * @param conversations all conversations found on FS at the moment
     * @return true if any were updated
     * @throws Exception this is bad
     */
    private boolean checkAndReactivateConversationsChanged(Map<String, Conversation> conversations)
        throws Exception
    {
        boolean reactivatedAny = false;

        for (Conversation activeConversation : activeConversations.values())
        {
            // Detect changes in conversation and reload if needed
            Conversation newConversation = conversations.get(activeConversation.getId());
            if (newConversation != null &&
                hasDifferencesInConfiguration(activeConversation, newConversation))
            {
                logger.info(String.format("Conversation '%s' has changes. Reloading ...",
                    activeConversation.getId()));
                routeManager.deactivate(activeConversation);
                routeManager.activate(newConversation);
                reactivatedAny = true;
            }
        }

        return reactivatedAny;
    }

    /**
     * @return true if there are no conversations activated in the system
     */
    private boolean noConversationsActivated()
    {
        return activeConversations == null || activeConversations.isEmpty();
    }

    /**
     * Activate given conversations
     * @param conversations conversations to be activated
     * @throws Exception camel errors
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
    protected boolean hasDifferencesInConfiguration(Conversation conv1, Conversation conv2)
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

        Map<String, Scenario> conv1Scenarios = conv1.getScenarios();
        Map<String, Scenario> conv2Scenarios = conv2.getScenarios();

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
        for (Scenario scenario1 : conv1Scenarios.values())
        {
            if (!conv2Scenarios.containsKey(scenario1.getConfigurationFilePath()))
            {
                String msg = String.format(
                        "Found changes in scenarios of conversation '%s'. Reloading ...",
                        conv1.getId());
                logger.info(msg);
                return true;
            }

            Scenario scenario2 = conv2Scenarios.get(scenario1
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
