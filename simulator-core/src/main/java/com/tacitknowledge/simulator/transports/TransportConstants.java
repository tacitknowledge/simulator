package com.tacitknowledge.simulator.transports;

/**
 * Contains the Simulator's default supported transport names/acronyms.
 * Use all-capitals.
 * Each supported transport will have its corresponding Transport implementation
 *
 * @author galo (jgalindo@tacitknowledge.com)
 */
public final class TransportConstants
{
    /**
     * Constant for FILE transport
     */
    public static final String FILE = "FILE";

    /**
     * Constant for FTP transport
     */
    public static final String FTP = "FTP";

    /**
     * Constant for JMS transport
     */
    public static final String JMS = "JMS";

    /**
     * Constant for REST IN transport
     */
    public static final String REST = "REST";

    /**
     * Constant for SOAP IN transport
     */
    public static final String SOAP = "SOAP";

    /**
     * Hidding the default constructor
     */
    private TransportConstants()
    {
    }
}
