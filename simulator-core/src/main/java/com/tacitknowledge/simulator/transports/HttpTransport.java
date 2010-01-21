package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.Configurable;
import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.Transport;
import com.tacitknowledge.simulator.TransportException;
import com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder;
import static com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder.name;
import static com.tacitknowledge.simulator.configuration.ParametersListBuilder.parameters;

import java.util.List;
import java.util.Map;

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
     * Transport parameters definition.
     */
    protected List<ParameterDefinitionBuilder.ParameterDefinition> parametersList =
        parameters().
            add(
                name(PARAM_PORT).
                    label("Port").
                    inOnly()
            ).
            add(
                name(PARAM_RESOURCE_URI).
                    label("Context root (include starting slash e.g.: /mytestapp").
                    required().
                    inOnly()
            ).
            add(
                name(PARAM_HTTP_OUT).
                    label("Is this an HTTP outbound transport?<br>"
                        + "This transport should only be used if the inbound transport is HTTP").
                    type(ParameterDefinitionBuilder.ParameterDefinition.TYPE_BOOLEAN).
                    outOnly().
                    defaultValue("true")
            );

    /**
     * Checks if http transport is used for output
     */
    private boolean isHttpOut = false;

    /**
     * Constructor
     * This constructor should only be called from the inheriting Transports.
     * @param type - Transport type
     */
    protected HttpTransport(final String type)
    {
        super(type);    
    }

    /**
     * Constructor.
     *
     * @param bound - specifies if transport is for in or out
     * @param type - transport type
     * @param parameters @see #parameters
     */
    protected HttpTransport(final int bound, final String type,
                            final Map<String, String> parameters)
    {
        super(bound, type, parameters);
    }

    /**
     * Sets and/or overrides instance variables from provided parameters and validates that
     * the required parameters are present.
     *
     * @throws com.tacitknowledge.simulator.TransportException
     *          If any required parameter is missing or incorrect
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
     * Returns a valid String URI representation of this transport for Camel route creation e.g.:
     * file://path/to/file/directory , jms:queue/myqueue ,
     *
     * @return URI representation of the transport
     * @throws TransportException If a required parameter is missing or not properly formatted.
     * @throws com.tacitknowledge.simulator.TransportException
     *          If any other error occurs.
     */
    @Override
    public String toUriString() throws ConfigurableException, TransportException
    {
        validateParameters();

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

        return sb.toString();
    }
}
