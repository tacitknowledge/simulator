package com.tacitknowledge.simulator;


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
     * @param id the id of the conversation
     * @param inboundTransport the inbound transport of the conversation
     * @param outboundTransport the outbound transport of the conversation
     * @param inboundFormat the inbound format of the conversation
     * @param outboundFormat the outbound format of the conversation
     * @return the created conversation object
     * @throws SimulatorException in case of an error
     */
    Conversation createConversation(Integer id, Transport inboundTransport,
            Transport outboundTransport, String inboundFormat, String outboundFormat)
            throws SimulatorException;

    /**
     * Creates a new scenario for an existing conversation
     *
     * @param conversationId the id of the conversation to be created
     * @param language
     *            The scripting language for the scenario. This would be System wide.
     * @param criteria
     *            The criteria script
     * @param transformation
     *            The transformation script
     */
    void createConversationScenario(int conversationId, String language, String criteria,
            String transformation);

    /**
     * Activates the conversation with provided conversation id.
     *
     * @param conversationId
     *            id of the conversation to be activated.
     * @throws ConversationNotFoundException in case the conversation is not found
     * @throws SimulatorException in case there is an error activating the conversation
     */
    void activate(int conversationId) throws ConversationNotFoundException, SimulatorException;

    /**
     * Deactivates the conversation with provided conversation id.
     *
     * @param conversationId
     *            id of the conversation to be deactivated.
     * @throws ConversationNotFoundException in case the conversation is not found
     * @throws SimulatorException in case there is an error deactivating the conversation
     */
    void deactivate(int conversationId) throws ConversationNotFoundException, SimulatorException;

    /**
     * Deactivates and stops and removes conversation data from memory
     *
     * @param conversationId id of the conversation to delete
     * @throws SimulatorException in case there is an error deleting the conversation
     */
    void deleteConversation(int conversationId) throws SimulatorException;
}
