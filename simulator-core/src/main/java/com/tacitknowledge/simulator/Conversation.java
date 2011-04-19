package com.tacitknowledge.simulator;

import java.util.Map;

import org.apache.camel.Exchange;

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
     * Adds a Scenario to this conversation
     * 
     * @param scenario scenario
     */
    void addScenario(Scenario scenario);
    
    void process(Exchange exchange) throws Exception;

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
    Map<String, Scenario> getScenarios();
}
