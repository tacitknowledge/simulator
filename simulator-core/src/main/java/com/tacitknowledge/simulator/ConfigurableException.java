package com.tacitknowledge.simulator;

/**
 * @author galo
 */
public class ConfigurableException extends SimulatorException
{
    /**
     * Default constructor
     * @param s String
     */
    public ConfigurableException(final String s)
    {
        super(s);
    }

    /**
     * Consrtuctor
     * @param s a String
     * @param throwable if something goes wrong
     */
    public ConfigurableException(final String s, final Throwable throwable)
    {
        super(s, throwable);
    }
}
