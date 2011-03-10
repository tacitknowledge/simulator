package com.tacitknowledge.simulator;

/**
 * Defines the interface for the implementations of the ConversationManager.
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public interface ConversationManager
{
    /**
     * Constructor. Creates a new Conversation from the provided transports and formats. If the id
     * field is set and a conversation with that id exists, attributes are updated
     *
     * @param id                the id of the conversation
     * @param name Name
     * @param inboundTransport  the inbound transport of the conversation
     * @param outboundTransport the inbound transport of the conversation
     * @param inAdapter         the outbound adapter of the conversation
     * @param outAdapter        the outbound transport of the conversation
     * @param defaultResponse   @return the created conversation object
     * @return The created or updated Conversation
     * @throws SimulatorException in case of an error
     */
    Conversation createOrUpdateConversation(String id, 
                                            Transport inboundTransport,
                                            Transport outboundTransport, 
                                            Adapter inAdapter,
                                            Adapter outAdapter);

    /**
     * Creates a new scenario for an existing conversation
     *
     * @param id the id of the conversation to be created
     * @param scenarioId     The unique scenario id
     * @param language       The scripting language for the scenario. This would be System wide.
     * @param criteria       The criteria script
     * @param transformation The transformation script
     * @return The created or updated Scenario
     */
    ConversationScenario createOrUpdateConversationScenario(String id, 
                                                            int scenarioId,
                                                            String language, 
                                                            String criteria,
                                                            String transformation);

    /**
     * Activates the conversation with provided conversation id.
     *
     * @param conversationId id of the conversation to be activated.
     * @throws ConversationNotFoundException in case the conversation is not found
     * @throws SimulatorException            in case there is an error activating the conversation
     */
    void activate(String conversationId) throws ConversationNotFoundException, SimulatorException;

    /**
     * Deactivates the conversation with provided conversation id.
     *
     * @param conversationId id of the conversation to be deactivated.
     * @throws SimulatorException in case there is an error deactivating the conversation
     */
    void deactivate(String conversationId) throws SimulatorException;

    /**
     * Deactivates and stops and removes conversation data from memory
     *
     * @param conversationId id of the conversation to delete
     * @throws SimulatorException in case there is an error deleting the conversation
     */
    void deleteConversation(String conversationId) throws SimulatorException;

    /**
     * This method creates an instance of the Class given in the name
     *
     * @param name to get an instance for
     * @return an instance of this ClassName
     * @throws ClassNotFoundException If class with the given name was not found
     * @throws IllegalAccessException If the class is accessed from an incorrect context
     * @throws InstantiationException If the class cannot be instantiated
     */
    Object getClassByName(String name)
        throws ClassNotFoundException, IllegalAccessException, InstantiationException;

    /**
     * Method to determine if a conversation is active or not
     *
     * @param conversationId of the conversation we want to know if it's active or not
     * @return true is it's active, false if inactive
     * @throws SimulatorException is there is a problem finding the conversation
     */
    boolean isActive(String conversationId) throws SimulatorException;

    /**
     *
     * @return List of available scripting languages
     */
    String[][] getAvailableLanguages();

    /**
     * deletes scenario from list of scenarios
     *
     * @param conversationId Conversation Id
     * @param scenarioId Scenario Id
     */
    void deleteScenario(String conversationId, int scenarioId);

    /**
     * @param conversationId Conversation Id
     * @return True is a Conversation with the given Id is found. False otherwise
     */
    boolean conversationExists(String conversationId);

    /**
     * Register all listeners specifying a location
     *
     * @param fileLocation Listener file location and name
     */
    void registerListeners(String fileLocation);
}
