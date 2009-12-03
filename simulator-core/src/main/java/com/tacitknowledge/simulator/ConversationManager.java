package com.tacitknowledge.simulator;

import java.util.List;


/**
 * Defines the interface for the implementations of the ConversationManager.
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public interface ConversationManager
{
    /**
     * Constructor. Creates a new Conversation from the provided transports and formats
     *
     * @param id                the id of the conversation
     * @param inboundTransport  the inbound transport of the conversation
     * @param outboundTransport the inbound transport of the conversation
     * @param inAdapter         the outbound adapter of the conversation
     * @param outAdapter        the outbound transport of the conversation
     * @return the created conversation object
     * @throws SimulatorException in case of an error
     */
    Conversation createConversation(Integer id, Transport inboundTransport,
        Transport outboundTransport, Adapter inAdapter,
        Adapter outAdapter)throws SimulatorException;

    /**
     * Creates a new scenario for an existing conversation
     *
     * @param conversationId the id of the conversation to be created
     * @param scenarioId
     * @param language       The scripting language for the scenario. This would be System wide.
     * @param criteria       The criteria script
     * @param transformation The transformation script
     * @return
     */
    ConversationScenario createOrUpdateConversationScenario(int conversationId, int scenarioId, String language, String criteria,
                                    String transformation);

    /**
     * Activates the conversation with provided conversation id.
     *
     * @param conversationId id of the conversation to be activated.
     * @throws ConversationNotFoundException in case the conversation is not found
     * @throws SimulatorException            in case there is an error activating the conversation
     */
    void activate(int conversationId) throws ConversationNotFoundException, SimulatorException;

    /**
     * Deactivates the conversation with provided conversation id.
     *
     * @param conversationId id of the conversation to be deactivated.
     * @throws SimulatorException            in case there is an error deactivating the conversation
     */
    void deactivate(int conversationId) throws SimulatorException;

    /**
     * Deactivates and stops and removes conversation data from memory
     *
     * @param conversationId id of the conversation to delete
     * @throws SimulatorException in case there is an error deleting the conversation
     */
    void deleteConversation(int conversationId) throws SimulatorException;

    /**
     * @param format The format the adapter is needed for
     * @return The parameter descriptions list
     * @see Adapter#getParametersList()
     * @see com.tacitknowledge.simulator.formats.AdapterFactory#getAdapterParameters(String)
     */
    List<List> getAdapterParameters(String format);

    /**
     * @param type The transport type
     * @return The parameters descriptions list
     * @see Transport#getParametersList()
     */
    List<List> getTransportParameters(String type);

    /**
     * This method creates an instance of the Class given in the name
     *
     * @param name to get an instance for
     * @return an instance of this ClassName
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    Object getClassByName(String name) throws ClassNotFoundException, IllegalAccessException, InstantiationException;

    /**
     * Method to determine if a conversation is active or not
     *
     * @param conversationId of the conversation we want to know if it's active or not
     * @return true is it's active, false if inactive
     * @throws SimulatorException is there is a problem finding the conversation
     */
    boolean isActive(int conversationId) throws SimulatorException;

    /**
     * returns available scripting languages
     * @return
     */
    String[][] getAvailableLanguages();

    /**
     * deletes scenario from list of scenarios
     * @param conversationId 
     * @param scenarioId
     */
    void deleteScenario(int conversationId, int scenarioId);

    /**
     *
     * @param conversationId
     * @return
     */
    boolean conversationExists(int conversationId);
}
