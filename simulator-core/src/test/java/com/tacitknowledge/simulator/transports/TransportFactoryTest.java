package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.formats.AdapterFactory;
import junit.framework.TestCase;

/**
 * @author galo
 */
public class TransportFactoryTest extends TestCase
{
    public void testShouldGetNullWithWrongTransportType()
    {
        assertNull(
                AdapterFactory.getInstance().getAdapter("SOMETRANSPORT")
        );
    }

    public void testGetFileTransport()
    {
        assertTrue(
                TransportFactory.getInstance().getTransport(TransportConstants.FILE) 
                        instanceof FileTransport
        );
    }

    public void testGetFtpTransport()
    {
        assertTrue(
                TransportFactory.getInstance().getTransport(TransportConstants.FTP)
                        instanceof FtpTransport
        );
    }

    public void testGetJsmTransport()
    {
        assertTrue(
                TransportFactory.getInstance().getTransport(TransportConstants.JMS)
                        instanceof JmsTransport
        );
    }

    public void testGetRestTransport()
    {
        assertTrue(
                TransportFactory.getInstance().getTransport(TransportConstants.REST)
                        instanceof RestTransport
        );
    }

    public void testGetSoapTransport()
    {
        assertTrue(
                TransportFactory.getInstance().getTransport(TransportConstants.SOAP)
                        instanceof SoapTransport
        );
    }
}
