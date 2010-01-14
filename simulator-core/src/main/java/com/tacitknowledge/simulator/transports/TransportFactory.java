package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.Transport;

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
     * @inheritDoc
     */
    private TransportFactory()
    {
    }

    /**
     * Container for the transports
     */
    private static Map<String, Transport> transports = new HashMap<String, Transport>()
    {
        {
            put(TransportConstants.FILE, new FileTransport());
            put(TransportConstants.FTP, new FtpTransport());
            put(TransportConstants.JMS, new JmsTransport());
            put(TransportConstants.REST, new RestTransport());
            put(TransportConstants.SOAP, new RestTransport());
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
        return transports.get(type);
    }

    public static List<List> getTransportParameters(String type)
    {
        List<List> list = null;
        // --- Transports should have been set with all-capitals
        if (transports.get(type.toUpperCase()) != null)
        {
            list = transports.get(type.toUpperCase()).getParametersList();
        }
        return list;
    }
}
