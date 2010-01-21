package com.tacitknowledge.simulator;

/**
 * Wrapper for any exception inside simulator framework
 *
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 */
public class SimulatorException extends Exception
{
    /**
     * Constructor for the SimulatorException class
     *
     * @param s exception message
     */
    public SimulatorException(final String s)
    {
        super(s);
    }

    /**
     * Constructor for the SimulatorException class
     *
     * @param s         exception message
     * @param throwable the original exception
     */
    public SimulatorException(final String s, final Throwable throwable)
    {
        super(s, throwable);
    }
}
