package com.tacitknowledge.simulator;

import java.util.List;

/**
 * Defines the contract for the Conversation.
 *
 * @author Alexandru Dereveanco (adereveanco@tacitknowledge.com)
 *
 */
public interface Conversation
{

    /**
     * Adds a new Scenario to this Conversation
     *
     * @param language
     *            The scripting language to be used.
     * @param criteria criteria script to be executed by the simulator
     * @param transformation transformation script to be executed by the simulator
     * @return ConversationScenario conversation scenario added to the conversation
     */
    ConversationScenario addScenario(String language, String criteria,
            String transformation);

    /**
     * Returns this conversation inbound transport
     *
     * @return inbound transport
     */
    Transport getInboundTransport();

    /**
     * Returns this conversation outbound transport
     *
     * @return outbound transport
     */
    Transport getOutboundTransport();

    /**
     * Retuns this conversation inbound format adapter
     *
     * @return inbound adapter
     */
    Adapter getInboundAdapter();

    /**
     * Retuns this conversation outbound format adapter
     *
     * @return outbound adapter
     */
    Adapter getOutboundAdapter();

    /**
     * Gets the unique id of the conversation based on the parameters provided
     * on object construction. Needed to ensure that RouteManager doesn't add
     * two identic routes to camel.
     * @return unique id based on the parameters of the object.
     * @throws SimulatorException If anything goes wrong.
     */
    String getUniqueId() throws SimulatorException;

    /**
     *
     *
     * @return unique id
     * @throws SimulatorException If anything goes wrong.
     */
    int getId();


    /**
     * Returns the current list of configured scenarios for this conversation
     *
     * @return a list of ConversationScenarios
     */
    List<ConversationScenario> getScenarios();

}
