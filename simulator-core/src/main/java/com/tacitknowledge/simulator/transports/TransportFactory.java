package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.Transport;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory implementation for Transport implementations
 *
 * @author galo (jgalindo@tacitknowledge.com)
 */
public class TransportFactory
{
    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(TransportFactory.class);

    /**
     * @inheritDoc
     */
    private TransportFactory()
    {
    }

    /**
     * Container for the transports
     */
    private static Map<String, Class> transports = new HashMap<String, Class>()
    {
        {
            put(TransportConstants.FILE, FileTransport.class);
            put(TransportConstants.FTP, FtpTransport.class);
            put(TransportConstants.JMS, JmsTransport.class);
            put(TransportConstants.REST, RestTransport.class);
            put(TransportConstants.SOAP, RestTransport.class);
        }
    };

    /**
     * Returns implementation of the transport for the provided type.
     *
     * @param type The transport type. @see com.tacitknowledge.simulator.TransportConstants
     * @return Transport for the specified type or null if the transport is not supported.
     */
    public static Transport getTransport(String type)
    {
        Transport transport = null;
        try
        {
            transport = (Transport) transports.get(type.toUpperCase()).newInstance();
        }
        catch(Exception e)
        {
            logger.error("Unexpected error trying to instantiate adapter " + type +
                    ": " + e.getMessage());
        }
        return transport;
    }

    public static List<List> getTransportParameters(String type) throws ConfigurableException
    {
        List<List> list = null;
        // --- Transports should have been set with all-capitals
        Transport transport = getTransport(type);
        if (transport != null)
        {
            list = transport.getParametersList();
        }
        return list;
    }
}
