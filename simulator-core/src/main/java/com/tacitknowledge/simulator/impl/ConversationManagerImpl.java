package com.tacitknowledge.simulator.impl;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.ConversationManager;
import com.tacitknowledge.simulator.ConversationNotFoundException;
import com.tacitknowledge.simulator.ConversationScenario;
import com.tacitknowledge.simulator.RouteManager;
import com.tacitknowledge.simulator.SimulatorException;
import com.tacitknowledge.simulator.Transport;
import com.tacitknowledge.simulator.configuration.EventDispatcher;
import com.tacitknowledge.simulator.configuration.SimulatorEventListener;
import com.tacitknowledge.simulator.camel.RouteManagerImpl;
import com.tacitknowledge.simulator.formats.AdapterFactory;
import com.tacitknowledge.simulator.scripting.ScriptExecutionService;
import com.tacitknowledge.simulator.transports.TransportFactory;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.*;
import java.lang.reflect.Constructor;

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
    private Map<Integer, ConversationImpl> conversations;


    private static ConversationManager instance;


    public static ConversationManager getInstance()
    {
        if(instance == null){
            instance = new ConversationManagerImpl();
        }
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
        this.conversations = new HashMap<Integer, ConversationImpl>();

    }

    /**
     * Constructor.
     * With this constructor, the manager will create its own RouteManager
     */
    public ConversationManagerImpl()
    {
        this.routeManager = new RouteManagerImpl();
        this.conversations = new HashMap<Integer, ConversationImpl>();
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

        }
        catch (ConversationNotFoundException cne)
        {
            //do nothing
        }
        catch (Exception e)
        {
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
    public void deleteConversation(int conversationId) throws SimulatorException
    {
        try
        {
            ConversationImpl conversation = conversations.get(conversationId);
            if (conversation != null)
            {
                routeManager.delete(conversation);
                conversations.remove(conversationId);
            }
        }
        catch (Exception e)
        {
            throw new SimulatorException("", e);
        }
    }

    public void deleteScenario(int conversationId, int scenarioId)
    {
        ConversationImpl conversation = conversations.get(conversationId);
        if (conversation != null)
        {
            Collection<ConversationScenario> scenarios = conversation.getScenarios();
            for (Iterator<ConversationScenario> iterator = scenarios.iterator(); iterator.hasNext();)
            {
                ConversationScenario scenario = iterator.next();
                if (scenario.getScenarioId() == scenarioId)
                {
                    iterator.remove();
                    break;
                }
            }

        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean conversationExists(int conversationId)
    {
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
    public Object getClassByName(String name) throws ClassNotFoundException, IllegalAccessException, InstantiationException
    {
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

    public String[][] getAvailableLanguages()
    {
        return ScriptExecutionService.getAvailableLanguages();
    }

    /**
     * {@inheritDoc}
     */
    public void registerListeners(String filePath) {
        if(filePath != null) {
            File file = new File(filePath);
            if(file.exists() && file.canRead()) {
                BufferedReader reader = null;
                try {
                     reader = new BufferedReader(new FileReader(file));
                    String line = null;
                    while((line = reader.readLine()) != null) {
                        line = line.trim();
                        if(line.length() > 0 && !line.startsWith("#")) {
                            registerListenerImplementation(line);
                        }
                    }
                } catch(IOException ex) {
                    logger.info("Exception while trying to read listener file.", ex);
                } finally {
                    if(null != reader) {
                        try {
                            reader.close();
                        } catch (IOException e) {}
                    }
                }
            } else {
                logger.info("Listeners not registered. Listener file not accesible.");
            }
        } else {
            logger.info("Listeners not registered. Listener file location is null.");
        }

    }

    /**
     * Register a SimulatorEventListener implementation in the EventDispatcher 
     * @param className - Qualified Class Name
     */
    private void registerListenerImplementation(String className) {
        Class listenerClass = null;
        try {
            listenerClass = Class.forName(className);
            Object instanceObject = listenerClass.newInstance();
            EventDispatcher.getInstance().addSimulatorEventListener((SimulatorEventListener)instanceObject);
            logger.info("Registered class: " + listenerClass);
        } catch (Exception e) {
            logger.info("Unable to register listener class: " + className);
        }
    }
}
