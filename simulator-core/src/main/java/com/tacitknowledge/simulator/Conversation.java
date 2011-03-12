package com.tacitknowledge.simulator;

import java.util.Collection;

/**
 * Defines the contract for the Conversation.
 *
 * @author Alexandru Dereveanco (adereveanco@tacitknowledge.com)
 */
public interface Conversation
{
    String SCENARIO_FILE_EXTENSION = ".scn";

    String INBOUND_CONFIG = "inbound.properties";

    String OUTBOUND_CONFIG = "outbound.properties";

    /**
     * returns the conversation ID
     * @return id
     */
    String getId();

    /**
     * returns last modified date of inbound.properties file
     * @return long
     */
    long getIboundModifiedDate();

    /**
     * returns last modified date of outbound.properties file
     * @return long
     */
    long getOutboundModifiedDate();

    /**
     * Adds a Scenario to this Conversation
     * 
     * @param language       The scripting language to be used.
     *                      if scenario already exists - will be ignored???
     * @param criteria       criteria script to be executed by the simulator
     * @param transformation transformation script to be executed by the simulator
     * @return ConversationScenario conversation scenario added to the conversation
     */
    ConversationScenario addScenario(final String language, final String criteria,
            final String transformation);

    /**
     * Adds a Scenario to this conversation
     * 
     * @param scenario scenario
     */
    void addScenario(ConversationScenario scenario);

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
     * Returns the current list of configured scenarios for this conversation
     *
     * @return a list of ConversationScenarios
     */
    Collection<ConversationScenario> getScenarios();
}
