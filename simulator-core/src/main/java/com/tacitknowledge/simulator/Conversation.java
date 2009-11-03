package com.tacitknowledge.simulator;

import com.tacitknowledge.simulator.adapters.Adapter;

import java.util.List;

/**
 * The Simulator conversation as set up by the user.
 * Works as a wrapper arround Camel route definition for entry and exit endpoints.
 * @author galo
 */
public class Conversation {
    /**
     * 
     */
    private Transport entryTransport;
    /**
     *
     */
    private Transport exitTransport;
    /**
     *
     */
    private Adapter entryAdapter;
    /**
     *
     */
    private Adapter exitAdapter;

    /**
     * List of configured scenarios for this conversation
     */
    private List<ConversationScenario> scenarios;

    public void createScenario(String language, String criteria, String transformation) {

    }
}
