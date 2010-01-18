package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.Configurable;
import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.StructuredSimulatorPojo;
import com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder;
import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Part;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tacitknowledge.simulator.configuration.ParametersListBuilder.parameters;
import static com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder.name;

/**
 * @author galo
 */
public class SoapAdapter extends XmlAdapter implements Adapter<Object>
{
    /**
     * WSDL URL parameter. OPTIONAL.
     */
    public static final String PARAM_WSDL_URL = "wdslURL";

    public static final String DEFAULT_PAYLOAD_KEY = "payload";

    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(SoapAdapter.class);

    /**
     * Adapter parameters definition.
     */
    private List<ParameterDefinitionBuilder.ParameterDefinition> parametersList =
            parameters().
                    add(
                            name(PARAM_WSDL_URL).
                                    label("WSDL URL").
                                    required()
                    );

    /**
     * Key for the root where to contain the SOAP message's payload
     */
    private String payloadKey = DEFAULT_PAYLOAD_KEY;

    /**
     * WSDL service definition.
     * Will be generated from the provided WSDL.
     */
    private Definition definition;

    /**
     * Available operations defined in the provided WSDL.
     */
    private Map<String, BindingOperation> availableOps = new HashMap<String, BindingOperation>();

    /**
     * Constructor
     */
    public SoapAdapter()
    {
        super(false);
    }

    /**
     * Constructor
     *
     * @param bound Configurable bound
     * @param parameters @see #parameters
     */
    public SoapAdapter(int bound, Map<String, String> parameters)
    {
        super(bound, parameters, false);
    }

    /**
     * 
     * @param o The String representation of the SOAP message
     * @return The generated SimulatorPojo
     * @throws FormatAdapterException If any error during the process occurs
     */
    protected SimulatorPojo createSimulatorPojo(String o)
        throws FormatAdapterException
    {
        logger.debug("Attempting to generate SimulatorPojo from SOAP content:\n" + o);

        SimulatorPojo pojo = new StructuredSimulatorPojo();

        try
        {
            MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

            InputStream is = new ByteArrayInputStream(o.getBytes("UTF-8"));
            // TODO - SO WHAT ABOUT THE HEADERS?
            SOAPMessage message = factory.createMessage(null, is);

            // --- So, now we got the SOAP message parsed.
            SOAPBody body = message.getSOAPBody();

            pojo.getRoot().put(payloadKey, getStructuredChilds(body));

            // --- Check that the passed methods/parameters are WSDL-valid
            validateOperationsAndParameters(pojo);

        }
        catch(SOAPException se)
        {
            String errorMessage = "Unexpected SOAP exception: " + se.getMessage();
            logger.error(errorMessage, se);
            throw new FormatAdapterException(errorMessage, se);
        }
        catch(UnsupportedEncodingException uee)
        {
            String errorMessage = "Unsupported encoding exception: " + uee.getMessage();
            logger.error(errorMessage, uee);
            throw new FormatAdapterException(errorMessage, uee);
        }
        catch (IOException ioe)
        {
            String errorMessage = "Unexpected IO exception: " + ioe.getMessage();
            logger.error(errorMessage, ioe);
            throw new FormatAdapterException(errorMessage, ioe);
        }

        return pojo;
    }

    /**
     * @inheritDoc
     * @param o
     * @return
     * @throws FormatAdapterException
     */
    @Override
    public SimulatorPojo createSimulatorPojo(Exchange o) throws FormatAdapterException
    {
        return createSimulatorPojo(o.getIn().getBody(String.class));
    }

    /**
     * Sets and/or overrides instance variables from provided parameters and validates that
     * the required parameters are present.
     *
     * @throws com.tacitknowledge.simulator.ConfigurableException
     *          If any required parameter is missing or incorrect
     */
    @Override
    protected void validateParameters() throws ConfigurableException
    {
        if (getParamValue(PARAM_WSDL_URL) == null)
        {
            throw new ConfigurableException("WSDL URL parameter is required");
        }

        getWSDLDefinition();
    }

    /**
     * Downloads and reads a WSDL from the provided URI
     * @throws ConfigurableException If the WSDL file was not in the provided WSDL URL or is wrong
     */
    private void getWSDLDefinition() throws ConfigurableException {
        try
        {
            WSDLFactory wsdlFactory = WSDLFactory.newInstance();
            WSDLReader wsdlReader = wsdlFactory.newWSDLReader();

            //wsdlReader.setFeature("javax.wsdl.verbose", false);
            //wsdlReader.setFeature("javax.wsdl.importDocuments", true);

            definition = wsdlReader.readWSDL(getParamValue(PARAM_WSDL_URL));
            if (definition == null)
            {
                throw new ConfigurableException(
                        "Definition element is null for WSDL URL: " +
                        getParamValue(PARAM_WSDL_URL));
            }

            getAvailableOperations();
        }
        catch(WSDLException we)
        {
            String errorMsg = "Unexpected WSDL error: " + we.getMessage();
            logger.error(errorMsg, we);
            throw new ConfigurableException(errorMsg, we);
        }
    }

    /**
     * Populates the list of available operations and their parts as defined in the provided WSDL
     */
    private void getAvailableOperations()
    {
        Map<QName, Binding> bindings = definition.getBindings();

        for (Map.Entry<QName, Binding> entry : bindings.entrySet())
        {
            List<BindingOperation> operations = entry.getValue().getBindingOperations();
            for (BindingOperation op : operations)
            {
                this.availableOps.put(op.getName(), op);
            }
        }
    }

    /**
     * 
     * @param pojo Generated SimulatorPojo
     * @throws FormatAdapterException If any validation fails.
     */
    private void validateOperationsAndParameters(SimulatorPojo pojo) throws FormatAdapterException
    {
        // --- Review all Methods passed in the SOAP message
        for (Map.Entry<String, Map> operationEntry :
                ((Map<String, Map>)pojo.getRoot().get(payloadKey)).entrySet())
        {
            String opName = operationEntry.getKey();
            
            if (!availableOps.containsKey(opName))
            {
                // --- If the requested Operation is no available, throw an error
                throw new FormatAdapterException(
                        "The requested service operation is not available in the provided WSDL: " +
                        opName);
            }

            BindingOperation availableOp = availableOps.get(opName);
            Map<String, Part> partsInAvailableOp =
                    availableOp.getOperation().getInput().getMessage().getParts();
            // --- If the operationEntry is output, get the parts from the output message
            if (getBound() == Configurable.BOUND_OUT)
            {
                partsInAvailableOp =
                        availableOp.getOperation().getOutput().getMessage().getParts();
            }

            // --- Now check that the passed parameters belong to the operationEntry in the proper
            // bound context
            // --- Everything in the payload Map, should be a Map in turn :
            //      {payload} >> {operationEntry} >> {part}
            Map<String, Object> op = operationEntry.getValue();
            for (Map.Entry<String, Object> part : op.entrySet())
            {
                // --- If the passed part if not part of the operationEntry, throw an error
                if (!partsInAvailableOp.containsKey(part.getKey()))
                {
                    String errorMsg = "The parameter " + part.getKey() +
                            " passed for method " + opName +
                            " in the SOAP message is not defined in the provided WSDL.";

                    logger.error(errorMsg);
                    throw new FormatAdapterException(errorMsg);
                }
            }
        }
    }

    /**
     * Returns a List of parameters the implementing instance uses.
     * Each list element is itself a List to describe the parameter as follows:
     * <p/>
     * - 0 : Parameter name
     * - 1 : Parameter description. Useful for GUI rendition
     * - 2 : Parameter type. Useful for GUI rendition.
     * - 3 : Required or Optional parameter. Useful for GUI validation.
     * - 4 : Parameter usage. Useful for GUI rendition.
     * - 5 : Default value
     *
     * @return List of Parameters for the implementing Transport.
     * @see com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder
     * @see com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder.ParameterDefinition
     */
    @Override
    public List<List> getParametersList()
    {
        return getParametersDefinitionsAsList(parametersList);
    }
}
