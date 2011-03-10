package com.tacitknowledge.simulator.transports;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.Transport;
import com.tacitknowledge.simulator.TransportException;

/**
 * @author galo
 */
public abstract class HttpTransport extends BaseTransport implements Transport
{
    /**
     * Resource URI parameter. REQUIRED
     */
    public static final String PARAM_RESOURCE_URI = "resourceURI";
    /**
     * HTTP out parameter.
     */
    public static final String PARAM_HTTP_OUT = "httpOut";
    /**
     * We use 0.0.0.0 instead of localhost to receive requests from any host.
     */
    private static final String HOST = "0.0.0.0";
    /**
     * Logger for this class.
     */
    private static Logger logger = LoggerFactory.getLogger(HttpTransport.class);

    /**
     * Checks if http transport is used for output
     */
    private boolean isHttpOut = false;

    /**
     * Constructor
     * This constructor should only be called from the inheriting Transports.
     *
     * @param type - Transport type
     */
    protected HttpTransport(final String type)
    {
        super(type);
    }

    /**
     * Constructor.
     *
     * @param bound      - specifies if transport is for in or out
     * @param type       - transport type
     * @param parameters @see #parameters
     */
    protected HttpTransport(final int bound, final String type,
                            final Map<String, String> parameters)
    {
        super(bound, type, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateParameters() throws ConfigurableException
    {
        if (getParamValue(PARAM_HTTP_OUT) != null)
        {
            this.isHttpOut = Boolean.parseBoolean(getParamValue(PARAM_HTTP_OUT));
        }

        if (!this.isHttpOut && getParamValue(PARAM_RESOURCE_URI) == null)
        {
            throw new ConfigurableException("Resource URI parameter is required");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getUriString() throws ConfigurableException, TransportException
    {
        // --- If this transport is an HTTP OUT, we just end the Camel route,
        // so we return the result from the execution script as the HTTP response body
        if (this.isHttpOut)
        {
            return "direct:end";
        }

        // --- 
        StringBuilder sb = new StringBuilder("jetty:http://");

        sb.append(HOST);

        if (getParamValue(PARAM_PORT) != null)
        {
            sb.append(":").append(getParamValue(PARAM_PORT));
        }

        sb.append(getParamValue(PARAM_RESOURCE_URI));
        sb.append("?matchOnUriPrefix=true");

        logger.info("Uri String: {}", sb.toString());

        return sb.toString();
    }
}
