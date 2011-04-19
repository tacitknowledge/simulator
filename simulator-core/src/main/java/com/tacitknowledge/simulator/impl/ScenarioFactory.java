package com.tacitknowledge.simulator.impl;

import com.tacitknowledge.simulator.Scenario;

public class ScenarioFactory
{
    /**
     * Creates a new ConversationScenario
     * 
     * @param fileName
     * @param language
     * @param condition
     * @param execute
     * @return A new conversation scenario
     */
     
    public Scenario createScenario(
        final String fileName,
        final String language,
        final String condition,
        final String execute)
    {
        if (fileName == null || language == null
            || condition == null || execute == null)
        {
            String errorMessage = "fileName, language, condition and execute"
                    + " are required for creating new conversation scenario.";

            throw new IllegalArgumentException(errorMessage);
        }

        return new ScenarioImpl(fileName, language, condition, execute);
    }
}
