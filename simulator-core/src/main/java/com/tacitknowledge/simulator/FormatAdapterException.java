package com.tacitknowledge.simulator;

/**
 * Exception to be thrown when the incoming data format is not correctly supported by the adapter
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class FormatAdapterException extends Exception
{
    /**
     * Constructor for the FormatAdapterException class
     * @param s exception message
     */
    public FormatAdapterException(String s)
    {
        super(s);
    }

    /**
     * Constructor for the FormatAdapterException class
     * @param s exception message
     * @param throwable the original exception
     */
    public FormatAdapterException(String s, Throwable throwable)
    {
        super(s, throwable);
    }
}