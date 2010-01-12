package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.Transport;
import com.tacitknowledge.simulator.TransportException;
import com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

import static com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder.name;
import static com.tacitknowledge.simulator.configuration.ParametersListBuilder.parameters;

/**
 * @author galo
 */
public class HttpTransport extends BaseTransport implements Transport
{
    /**
     * Resource URI parameter. REQUIRED
     */
    public static final String PARAM_RESOURCE_URI = "resourceURI";

    public static final String PARAM_HTTP_OUT = "httpOut";

    /**
     * Transport parameters definition.
     */
    private List<ParameterDefinitionBuilder.ParameterDefinition> parametersList =
        parameters().
            add(
                name(PARAM_HOST).
                    label("Host Name (without the protocol definition 'http://' " +
                            "nor ending slash .e.g.: myappserver.com))").
                    required().
                    inOnly()
            ).
            add(
                name(PARAM_PORT).
                    label("Port").
                    inOnly()
            ).
            add(
                name(PARAM_RESOURCE_URI).
                    label("Resource URI (include starting slash e.g.: /mytestapp").
                    required().
                    inOnly()
            ).
            add(
                name(PARAM_HTTP_OUT).
                    label("Is this an HTTP outbound transport?<br>" +
                        "This transport should only be used if the inbound transport is HTTP").
                    outOnly().
                    defaultValue("true")
            );

    private boolean isHttpOut = false;


    /**
     * Constructor. This constructor should be called from the implementing classes'
     * default constructor.
     *
     * @param
     */
    public HttpTransport()
    {
        super(TransportConstants.HTTP);
    }

    /**
     * Constructor.
     *
     * @param parameters @see #parameters
     */
    public HttpTransport(Map<String, String> parameters)
    {
        super(TransportConstants.HTTP, parameters);
    }

    /**
     * Sets and/or overrides instance variables from provided parameters and validates that
     * the required parameters are present.
     *
     * @throws com.tacitknowledge.simulator.TransportException
     *          If any required parameter is missing or incorrect
     */
    @Override
    void validateParameters() throws TransportException
    {
        if (getParamValue(PARAM_HTTP_OUT) != null)
        {
            this.isHttpOut = Boolean.parseBoolean(getParamValue(PARAM_HTTP_OUT));
        }

        if (!this.isHttpOut && getParamValue(PARAM_HOST) == null)
        {
            throw new TransportException("Host name parameter is required");
        }
        if (!this.isHttpOut && getParamValue(PARAM_RESOURCE_URI) == null)
        {
            throw new TransportException("Resource URI parameter is required");
        }
    }

    /**
     * Returns a List of parameters the implementing instance uses.
     * Each list element is itself a List to describe the parameter as follows:
     * - 0 : Parameter name
     * - 1 : Parameter description. Useful for GUI rendition
     * - 2 : Parameter type (string, date, boolean). Useful for GUI rendition.
     * - 3 : Required or Optional parameter. Useful for GUI validation.
     *
     * @return List of Parameters for the implementing Transport.
     */
    @Override
    public List<List> getParametersList()
    {
        return getParametersDefinitionsAsList(parametersList);
    }

    /**
     * Returns a valid String URI representation of this transport for Camel route creation e.g.:
     * file://path/to/file/directory , jms:queue/myqueue ,
     *
     * @return URI representation of the transport
     * @throws com.tacitknowledge.simulator.TransportException
     *          If a required parameter is missing or not properly formatted.
     */
    @Override
    public String toUriString() throws TransportException
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

        sb.append(getParamValue(PARAM_HOST));

        if (getParamValue(PARAM_PORT) != null)
        {
            sb.append(":").append(getParamValue(PARAM_PORT));
        }

        sb.append(getParamValue(PARAM_RESOURCE_URI));

        return sb.toString();
    }
}
