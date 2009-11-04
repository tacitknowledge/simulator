package com.tacitknowledge.simulator;

/**
 * wrapper for any exception inside simulator framework
 *
 * @author nikitabelenkiy
 */
public class SimulatorException extends Exception
{

    public SimulatorException(String s, Throwable throwable)
    {
        super(s, throwable);
    }


}
