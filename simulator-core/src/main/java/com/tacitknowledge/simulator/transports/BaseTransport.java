package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Base Transport class. Contains attributes common to all transport implementations
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public abstract class BaseTransport implements Transport
{
    // --- Common Transport parameters
    /**
     * Host name parameter.
     */
    public static final String PARAM_HOST = "host";

    /**
     * Port parameter.
     */
    public static final String PARAM_PORT = "port";

    /**
     * Username parameter. Username to login as.
     */
    public static final String PARAM_USERNAME = "username";

    /**
     * Password parameter. Used to login to the remote file system. OPTIONAL.
     */
    public static final String PARAM_PASSWORD = "password";

    /**
     * Ampersand constant
     */
    protected static final String AMP = "&";

    /**
     * Transport type
     */
    private String type;

    protected Configurable configurable;

    /**
     * Default Constructor
     * @param type type
     */
    protected BaseTransport(final String type)
    {
        this(type,new BaseConfigurable(Configurable.BOUND_IN,new HashMap<String, String>()));
    }

    /**
     * Constructor
     * @param type TYpe
     * @param configurable BaseConfigurable implementation
     */
    protected BaseTransport(String type, Configurable configurable) {
        this.type = type;
        this.configurable = configurable;
    }

    /**
     * Getter for @see #type
     *
     * @return @see #type
     */
    public String getType()
    {
        return type;
    }

    /**
     * 
     * @return The String representation of this Transport
     */
    @Override
    public String toString()
    {
        return "BaseTransport{"
            + "parameters=" + configurable.getParameters()
            + ", type='" + type + '\''
            + '}';
    }

    /**
     * {@inheritDoc}
     */
    public String toUriString() throws ConfigurableException, TransportException
    {
        validateParameters();

        return getUriString();
    }

    /**
     * Returns a valid String URI representation of this transport for Camel route creation e.g.:
     * file://path/to/file/directory , jms:queue/myqueue ,
     *
     * @return URI representation of the transport
     * @throws ConfigurableException If a required parameter is missing or not properly formatted.
     * @throws TransportException If any other error occurs
     */
    protected abstract String getUriString()
        throws ConfigurableException, TransportException;

    public abstract void validateParameters() throws ConfigurableException;

    public Configurable getConfigurable() {
        return configurable;
    }
}
