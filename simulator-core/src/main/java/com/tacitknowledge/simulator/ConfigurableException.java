package com.tacitknowledge.simulator;

/**
 * @author galo
 */
public class ConfigurableException extends SimulatorException
{
    public ConfigurableException(String s)
    {
        super(s);
    }

    public ConfigurableException(String s, Throwable throwable)
    {
        super(s, throwable);
    }
}
