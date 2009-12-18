package com.tacitknowledge.simulator;

/**
 * Exception to be thrown when a transport parameters are not properly set.
 *
 * @author galo
 */
public class TransportException extends Exception
{
    /**
     * Constructor
     *
     * @param s the exception message
     */
    public TransportException(String s)
    {
        super(s);
    }

    /**
     * @param s         exception message
     * @param throwable the original exception
     */
    public TransportException(String s, Throwable throwable)
    {
        super(s, throwable);
    }
}
