package com.tacitknowledge.simulator;

import java.util.Collection;

/**
 * Defines the contract for the Conversation.
 *
 * @author Alexandru Dereveanco (adereveanco@tacitknowledge.com)
 */
public interface Conversation
{

    /**
     * @return any name. will be used as log file name
     */
    String getName();

    /**
     * Adds or updates a Scenario to this Conversation
     *
     * @param scenarioId Scenario id
     * @param language       The scripting language to be used.
     *                      if scenario already exists - will be ignored???
     * @param criteria       criteria script to be executed by the simulator
     * @param transformation transformation script to be executed by the simulator
     * @return ConversationScenario conversation scenario added to the conversation
     */
    ConversationScenario addOrUpdateScenario(int scenarioId, String language, String criteria,
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
     * @return unique id
     */
    int getId();


    /**
     * Returns the current list of configured scenarios for this conversation
     *
     * @return a list of ConversationScenarios
     */
    Collection<ConversationScenario> getScenarios();

    /**
     * Default script. Will be executed before any scenario
     *
     * @return script code
     */
    String getDefaultResponse();
}
