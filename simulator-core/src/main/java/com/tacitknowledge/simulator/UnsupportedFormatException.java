package com.tacitknowledge.simulator;

/**
 * Exception used when the adapter doesn't support the format.
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class UnsupportedFormatException extends Exception
{
    /**
     * Constructor for the UnsupportedFormatException class
     *
     * @param s exception message
     */
    public UnsupportedFormatException(final String s)
    {
        super(s);
    }

    /**
     * Constructor for the UnsupportedFormatException class
     *
     * @param s         exception message
     * @param throwable the original exception
     */
    public UnsupportedFormatException(final String s, final Throwable throwable)
    {
        super(s, throwable);
    }
}
