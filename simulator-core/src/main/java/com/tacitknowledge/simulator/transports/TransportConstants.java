package com.tacitknowledge.simulator.transports;

/**
 * Contains the Simulator's default supported transport names/acronyms. Use all-capitals. Each
 * supported transport will have its corresponding Transport implementation
 *
 * @author galo (jgalindo@tacitknowledge.com)
 */
public final class TransportConstants
{
    /** Constant for FILE transport */
    public static final String FILE = "FILE";

    /** Constant for FTP transport */
    public static final String FTP = "FTP";

    /** Constant for SFTP transport */
    public static final String SFTP = "SFTP";

    /** Constant for FTPS transport */
    public static final String FTPS = "FTPS";


    /** Constant for JMS transport */
    public static final String JMS = "JMS";

    /** Constant for REST IN transport */
    public static final String REST = "REST";

    /** Constant for SOAP IN transport */
    public static final String SOAP = "SOAP";
    /** Constant for mock out transport( Request/Reply ) */
    public static final String MOCKOUT = "MOCKOUT";

    /** HTTP transport*/
    public static final String HTTP = "HTTP";


    /** Hidding the default constructor */
    private TransportConstants()
    {
    }
}
