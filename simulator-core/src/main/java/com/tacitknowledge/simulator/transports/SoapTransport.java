package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.Transport;
import com.tacitknowledge.simulator.TransportException;
import com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder;

import static com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder.name;
import static com.tacitknowledge.simulator.configuration.ParametersListBuilder.parameters;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * @author galo
 */
public class SoapTransport extends BaseTransport implements Transport
{
    /**
     * Service URL parameter. The URL to where the SOAP HTTP request is sent. REQUIRED.
     */
    public final static String PARAM_SERVICE_URL = "serviceUrl";

    /**
     * Service class parameter. Fully-qualified name of the serving class. OPTIONAL.
     */
    public final static String PARAM_SERVICE_CLASS = "serviceClass";

    /**
     * WSDL URL parameter. WSDL location. OPTIONAL.
     */
    public final static String PARAM_WSDL_URL = "wsdlUrl";

    /**
     * Service name parameter. OPTIONAL.
     */
    public final static String PARAM_SERVICE_NAME = "serviceName";

    /**
     * Port name parameter. OPTIONAL.
     */
    public final static String PARAM_PORT_NAME = "portName";

    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(SoapTransport.class);

    /**
     * Transport parameters definition.
     */
    private List<ParameterDefinitionBuilder.ParameterDefinition> parametersList =
        parameters().
            add(
                name(PARAM_SERVICE_URL) .
                label("Service URL").
                required()
            ).
            add(
                name(PARAM_SERVICE_CLASS).
                label("Service class")
            ).
            add(
                name(PARAM_WSDL_URL).
                label("WSDL location URL")
            ).
            add(
                name(PARAM_SERVICE_NAME).
                label("Service name")
            ).
            add(
                name(PARAM_PORT_NAME).
                label("Port name")
            );

    /**
     * Constructor. This constructor should be called from the implementing classes'
     * default constructor.
     *
     */
    public SoapTransport()
    {
        super(TransportConstants.SOAP);
    }

    /**
     * Constructor.
     *
     * @param parameters @see #parameters
     */
    public SoapTransport(Map<String, String> parameters)
    {
        super(TransportConstants.SOAP, parameters);
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
        // ---
        if (getParamValue(PARAM_SERVICE_URL) == null)
        {
            throw new TransportException("Service URL parameter is required");
        }
    }

    /**
     * @inheritDoc
     */
    public String toUriString() throws TransportException
    {
        validateParameters();

        StringBuilder sb = new StringBuilder("cxf://");

        // ---
        sb.append(getParamValue(PARAM_SERVICE_URL));

        // --- Options
        sb.append("?");
        if (getParamValue(PARAM_SERVICE_CLASS) != null)
        {
            sb.append("serviceClass=").append(getParamValue(PARAM_SERVICE_CLASS)).append(AMP);
        }
        if (getParamValue(PARAM_WSDL_URL) != null)
        {
            sb.append("wsdlURL=").append(getParamValue(PARAM_WSDL_URL)).append(AMP);
        }
        if (getParamValue(PARAM_SERVICE_NAME) != null)
        {
            sb.append("serviceName=").append(getParamValue(PARAM_SERVICE_NAME)).append(AMP);
        }
        if (getParamValue(PARAM_PORT_NAME) != null)
        {
            sb.append("portName=").append(getParamValue(PARAM_PORT_NAME)).append(AMP);
        }

        sb.append("dataFormat=POJO");
        
        return sb.toString();
    }

    /**
     * @inheritDoc
     */
    public List<List> getParametersList()
    {
        return getParametersDefinitionsAsList(parametersList);
    }
}
