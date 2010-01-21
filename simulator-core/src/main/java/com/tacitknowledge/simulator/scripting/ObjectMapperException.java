package com.tacitknowledge.simulator.scripting;

/**
 * @author nikitabelenkiy
 */
public class ObjectMapperException extends Exception
{
    /**
     * Default Constructor
     * @param s String
     */
    public ObjectMapperException(final String s)
    {
        super(s);
    }

    /**
     * Constructor
     * @param s Sring
     * @param throwable Exception
     */
    public ObjectMapperException(final String s, final Throwable throwable)
    {
        super(s, throwable);
    }
}
