package com.tacitknowledge.simulator.impl;

import com.tacitknowledge.simulator.*;
import com.tacitknowledge.simulator.camel.RouteManagerImpl;
import com.tacitknowledge.simulator.formats.AdapterFactory;
import com.tacitknowledge.simulator.scripting.ScriptExecutionService;
import com.tacitknowledge.simulator.transports.TransportFactory;
import org.apache.log4j.Logger;

import java.util.*;

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
    private static Logger logger = Logger.getLogger(ConversationManagerImpl.class);

    /**
     * CamelRoutes manager
     */
    private RouteManager routeManager;

    /**
     * Currently configured conversations
     */
    private Map<Integer, ConversationImpl> conversations = new HashMap<Integer, ConversationImpl>();


    private static final ConversationManager instance = new ConversationManagerImpl();


    public static ConversationManager getInstance() {
        return instance;
    }

    /**
     * Public constructor for ConversationManagerImpl.
     *
     * @param routeManager RouteManager to use
     */
    public ConversationManagerImpl(RouteManager routeManager)
    {
        this.routeManager = routeManager;
    }

    /**
     * Constructor.
     * With this constructor, the manager will create its own RouteManager
     */
    ConversationManagerImpl()
    {
        this.routeManager = new RouteManagerImpl();
    }

    /**
     * {@inheritDoc}
     */
    public Conversation createConversation(Integer id, String name, Transport inboundTransport,
                                           Transport outboundTransport, Adapter inAdapter, Adapter outAdapter, String defaultResponse)
        throws SimulatorException
    {
        ConversationImpl conversation = ConversationFactory.createConversation(id, name, inboundTransport,
                outboundTransport, inAdapter, outAdapter, defaultResponse);
        assert conversations.get(id) == null;

        conversations.put(id, conversation);

        logger.info("Created new conversation with id : " + id);

        return conversation;
    }

    /**
     * @param conversationId conversationId
     * @return conversation from the list of created conversations
     * @throws ConversationNotFoundException in case conversation is not found
     */
    private Conversation getConversationById(int conversationId)
            throws ConversationNotFoundException
    {
        Conversation conversation = conversations.get(conversationId);
        if (conversation == null)
        {
            logger.error("Conversation with id : " + conversationId + " is not found.");

            throw new ConversationNotFoundException("Conversation with id " + conversationId
                    + " is not created.");
        }
        return conversation;
    }

    /**
     * @param conversationId the id of the conversation to be created
     * @param scenarioId
     * @param language       The scripting language for the scenario. This would be System wide.
     * @param criteria       The criteria script
     * @param transformation The transformation script
     *
     * @return new scenario object. null if conversation does not exist
     */
    public ConversationScenario createOrUpdateConversationScenario(int conversationId, int scenarioId, String language, String criteria,
                                           String transformation)
    {
        Conversation conversation = conversations.get(conversationId);
        ConversationScenario conversationScenario = null;
        if (conversation != null)
        {
            String defaultResponse = conversation.getDefaultResponse();
            conversationScenario = conversation.addOrUpdateScenario(scenarioId, language, criteria,
                    defaultResponse == null ? transformation : defaultResponse + "\n" + transformation);
        }
        return conversationScenario;
    }

    /**
     * @param conversationId conversation id of the conversation to be activated.
     * @throws ConversationNotFoundException exception.
     * @throws SimulatorException            exception.
     * @inheritDoc
     */
    public void activate(int conversationId) throws ConversationNotFoundException,
            SimulatorException
    {
        Conversation conversation = getConversationById(conversationId);
        try
        {
            routeManager.activate(conversation);
            logger.debug("Activated conversation " + conversation);
        }
        catch (Exception e)
        {
            logger.error("Conversation with id : "
                    + conversationId + " couldn't be activated.", e);

            throw new SimulatorException("Conversation activation exception", e);
        }
    }

    /**
     * @inheritDoc
     */
    public void deactivate(int conversationId) throws SimulatorException
    {

        try
        {
            Conversation conversation = getConversationById(conversationId);
            routeManager.deactivate(conversation);
            conversations.remove(conversationId);
            logger.debug("Deactivated conversation " + conversation);

        } catch (ConversationNotFoundException cne){
           //do nothing
        } catch (Exception e) {
            logger.error("Conversation with id : "
                    + conversationId + " couldn't be deactivated.", e);
            throw new SimulatorException("Conversation deactivation exception:" + e.getMessage(), e);
        }

    }

    /**
     * @param conversationId conversation id of the conversation to be deleted.
     * @throws SimulatorException exception.
     * @inheritDoc
     */
    public void deleteConversation(int conversationId) throws SimulatorException {
        try {
            ConversationImpl conversation = conversations.get(conversationId);
            if (conversation != null) {
                routeManager.delete(conversation);
                conversations.remove(conversationId);
            }
        } catch (Exception e) {
            throw new SimulatorException("", e);
        }
    }

    public void deleteScenario(int conversationId, int scenarioId) {
        ConversationImpl conversation = conversations.get(conversationId);
        if (conversation != null) {
            Collection<ConversationScenario> scenarios = conversation.getScenarios();
            for (Iterator<ConversationScenario> iterator = scenarios.iterator(); iterator.hasNext();) {
                ConversationScenario scenario = iterator.next();
                if (scenario.getScenarioId() == scenarioId) {
                    iterator.remove();
                    break;
                }
            }

        }
    }

    /**
     * 
     * {@inheritDoc}
     */
    public boolean conversationExists(int conversationId) {
        return conversations.containsKey(conversationId);
    }

    /**
     * @param format @see ConversationManager#getAdapterParameters
     * @return @see ConversationManager#getAdapterParameters
     * @inheritDoc
     */
    public List<List> getAdapterParameters(String format)
    {
        return AdapterFactory.getAdapterParameters(format);
    }

    /**
     * @param type The transport type
     * @return @see ConversationManager#getTransportParameters
     * @inheritDoc
     */
    public List<List> getTransportParameters(String type)
    {
        return TransportFactory.getTransportParameters(type);
    }

    /**
     * @param name class name
     * @return instance of the class
     * @inheritDoc
     */
    public Object getClassByName(String name) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return Class.forName(name).newInstance();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isActive(int conversationId) throws SimulatorException
    {
        try
        {
            Conversation conversation = getConversationById(conversationId);
            return routeManager.isActive(conversation);
        }
        catch (ConversationNotFoundException e)
        {
            return false;
        }
    }

    public String[][] getAvailableLanguages() {
        return ScriptExecutionService.getAvailableLanguages();
    }


}
