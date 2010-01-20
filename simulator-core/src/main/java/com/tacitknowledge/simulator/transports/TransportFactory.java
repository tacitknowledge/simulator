package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.ConfigurableFactoryImpl;
import com.tacitknowledge.simulator.Transport;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory implementation for Transport implementations
 *
 * @see com.tacitknowledge.simulator.Transport
 * @see com.tacitknowledge.simulator.Configurable
 *
 * @author galo (jgalindo@tacitknowledge.com)
 */
public class TransportFactory extends ConfigurableFactoryImpl
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
    private static final Map<String, Class> transports = new HashMap<String, Class>()
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
        super(transports);
    }

    /**
     *
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
    public Transport getTransport(String type)
    {
        return (Transport) getConfigurable(type);
    }
}
