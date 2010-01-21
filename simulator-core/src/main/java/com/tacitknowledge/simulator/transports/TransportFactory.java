package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.ConfigurableFactoryImpl;
import com.tacitknowledge.simulator.Transport;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory implementation for Transport implementations
 *
 * @author galo (jgalindo@tacitknowledge.com)
 * @see com.tacitknowledge.simulator.Transport
 * @see com.tacitknowledge.simulator.Configurable
 */
public final class TransportFactory extends ConfigurableFactoryImpl
{
    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(TransportFactory.class);

    /**
     * Singleton instance
     */
    private static TransportFactory instance = null;


    /**
     * Container for the transports
     */
    private static final Map<String, Class> TRANSPORTS = new HashMap<String, Class>()
    {
        {
            put(TransportConstants.FILE, FileTransport.class);
            put(TransportConstants.FTP, FtpTransport.class);
            put(TransportConstants.JMS, JmsTransport.class);
            put(TransportConstants.REST, RestTransport.class);
            put(TransportConstants.SOAP, SoapTransport.class);
        }
    };

    /**
     * @inheritDoc
     */
    private TransportFactory()
    {
        super(TRANSPORTS);
    }

    /**
     * @return The singleton instance
     */
    public static TransportFactory getInstance()
    {
        if (instance == null)
        {
            instance = new TransportFactory();
        }
        return instance;
    }

    /**
     * Returns implementation of the transport for the provided type.
     *
     * @param type The transport type. @see com.tacitknowledge.simulator.TransportConstants
     * @return Transport for the specified type or null if the transport is not supported.
     */
    public Transport getTransport(final String type)
    {
        return (Transport) getConfigurable(type);
    }
}
