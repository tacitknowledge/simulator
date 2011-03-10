package com.tacitknowledge.simulator;

@SuppressWarnings("serial")
public class ScenarioParsingException extends Exception
{
    /**
     * Constructor for the ScenarioParsingException class
     *
     * @param s exception message
     */
    public ScenarioParsingException(final String s)
    {
        super(s);
    }

    /**
     * Constructor for the ScenarioParsingException class
     *
     * @param s         exception message
     * @param throwable the original exception
     */
    public ScenarioParsingException(final String s, final Throwable throwable)
    {
        super(s, throwable);
    }
}
