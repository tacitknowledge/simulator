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

    Conversation(Transport entryTransport, Transport exitTransport, Adapter entryAdapter, Adapter exitAdapter) {
        this.entryTransport = entryTransport;
        this.exitTransport = exitTransport;
        this.entryAdapter = entryAdapter;
        this.exitAdapter = exitAdapter;
    }

    public ConversationScenario addScenario(String language, String criteria, String transformation) {
       return null;
    }

    public Transport getEntryTransport() {
        return entryTransport;
    }

    public Transport getExitTransport() {
        return exitTransport;
    }

    public Adapter getEntryAdapter() {
        return entryAdapter;
    }

    public Adapter getExitAdapter() {
        return exitAdapter;
    }

    public List<ConversationScenario> getScenarios() {
        return scenarios;
    }
}
