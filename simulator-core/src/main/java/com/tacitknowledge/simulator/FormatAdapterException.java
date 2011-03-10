package com.tacitknowledge.simulator;

/**
 * Exception to be thrown when the incoming data format is not correctly supported by the adapter
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
@SuppressWarnings("serial")
public class FormatAdapterException extends SimulatorException
{
    /**
     * Constructor for the FormatAdapterException class
     *
     * @param s exception message
     */
    public FormatAdapterException(final String s)
    {
        super(s);
    }

    /**
     * Constructor for the FormatAdapterException class
     *
     * @param s         exception message
     * @param throwable the original exception
     */
    public FormatAdapterException(final String s, final Throwable throwable)
    {
        super(s, throwable);
    }
}
