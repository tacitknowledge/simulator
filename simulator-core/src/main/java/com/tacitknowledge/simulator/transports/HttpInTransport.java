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
public class HttpInTransport extends BaseTransport implements Transport
{
    /**
     * Host name parameter. REQUIRED.
     */
    public static final String PARAM_HOSTNAME = "hostName";

    /**
     * Port parameter. OPTIONAL.
     */
    public static final String PARAM_PORT = "port";

    /**
     * Resource URI parameter. REQUIRED
     */
    public static final String PARAM_RESOURCE_URI = "resourceURI";

    /**
     * Transport parameters definition.
     */
    private List<ParameterDefinitionBuilder.ParameterDefinition> parametersList =
        parameters().
            add(
                name(PARAM_HOSTNAME).
                    label("Host Name (without the protocol definition 'http://'").
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
                    label("Resource URI").
                    required()
            );


    /**
     * Constructor. This constructor should be called from the implementing classes'
     * default constructor.
     *
     * @param
     */
    public HttpInTransport()
    {
        super(TransportConstants.HTTP_IN);
    }

    /**
     * Constructor.
     *
     * @param parameters @see #parameters
     */
    public HttpInTransport(Map<String, String> parameters)
    {
        super(TransportConstants.HTTP_IN, parameters);
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
        if (getParamValue(PARAM_HOSTNAME) == null)
        {
            throw new TransportException("Host name parameter is required " +
                    "(without ending slash .e.g.: myappserver.com)");
        }
        if (getParamValue(PARAM_RESOURCE_URI) == null)
        {
            throw new TransportException("Resource URI parameter is required (e.g.: /orders/");
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

        StringBuilder sb = new StringBuilder("jetty:http://");

        sb.append(getParamValue(PARAM_HOSTNAME));

        if (getParamValue(PARAM_PORT) != null)
        {
            sb.append(":").append(getParamValue(PARAM_PORT));
        }

        sb.append(getParamValue(PARAM_RESOURCE_URI));

        return sb.toString();
    }
}
