package com.tacitknowledge.simulator;

/**
 * Exception to be thrown when a transport parameters are not properly set.
 *
 * @author galo
 */
@SuppressWarnings("serial")
public class TransportException extends SimulatorException
{
    /**
     * Constructor
     *
     * @param s the exception message
     */
    public TransportException(final String s)
    {
        super(s);
    }

    /**
     * @param s         exception message
     * @param throwable the original exception
     */
    public TransportException(final String s, final Throwable throwable)
    {
        super(s, throwable);
    }
}
