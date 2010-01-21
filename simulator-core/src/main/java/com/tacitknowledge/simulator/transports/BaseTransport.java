package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.BaseConfigurable;
import com.tacitknowledge.simulator.Configurable;
import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.Transport;
import com.tacitknowledge.simulator.TransportException;

import java.util.Map;

/**
 * Base Transport class. Contains attributes common to all transport implementations
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public abstract class BaseTransport extends BaseConfigurable implements Transport
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

    /**
     * Constructor
     * The default constructor should not be called. Implementing classes must set
     * transport type.
     *
     * @see com.tacitknowledge.simulator.transports.TransportConstants
     */
    private BaseTransport()
    {
    }

    /**
     * Default Constructor
     * @param type type
     */
    protected BaseTransport(final String type)
    {
        this.type = type;
    }

    /**
     * Constructor
     * @param type TYpe
     * @param parameters base parameters
     */
    protected BaseTransport(final String type, final Map<String, String> parameters)
    {
        super(parameters);
        this.type = type;
    }

    /**
     * Constructor.
     *
     * @param type       @see #type
     * @param bound in or out
     * @param parameters @see #parameters
     */
    protected BaseTransport(final int bound, final String type,
                            final Map<String, String> parameters)
    {
        super(bound, parameters);
        this.type = type;
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
            + "parameters=" + getParameters()
            + ", type='" + type + '\''
            + '}';
    }

    /**
     * Returns a valid String URI representation of this transport for Camel route creation e.g.:
     * file://path/to/file/directory , jms:queue/myqueue ,
     *
     * @return URI representation of the transport
     * @throws com.tacitknowledge.simulator.ConfigurableException
     *          If a required parameter is missing or not properly formatted.
     * @throws com.tacitknowledge.simulator.TransportException
     *          If any other error occurs
     */
    public String toUriString() throws ConfigurableException, TransportException
    {
        validateParameters();

        return null;
    }
}
