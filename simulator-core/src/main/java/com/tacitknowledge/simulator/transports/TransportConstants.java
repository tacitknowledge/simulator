package com.tacitknowledge.simulator.transports;

/**
 * Contains the Simulator's default supported transport names/acronyms.
 * Each supported transport will have its corresponding Transport implementation
 *
 * @author galo (jgalindo@tacitknowledge.com)
 */
public class TransportConstants
{
    /**
     * Constant for FILE transport
     */
    public static final String FILE = "file";

    /**
     * Constant for FTP transport
     */
    public static final String FTP = "ftp";

    /**
     * Constant for JMS transport
     */
    public static final String JMS = "jms";

    /**
     * Hidding the default constructor
     */
    private TransportConstants()
    {
    }
}
