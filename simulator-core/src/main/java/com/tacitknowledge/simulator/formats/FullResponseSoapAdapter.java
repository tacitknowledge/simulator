package com.tacitknowledge.simulator.formats;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Part;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.StructuredSimulatorPojo;

/**
 * This class is based on the logic in SoapAdapter with the difference that the full response is expected
 * to be given in the scenario rather than being built in this class.
 * This provides full flexibility and direct handling of complex namespaces that can occur in the elements.
 * 
 * TODO:
 * Add configuration to be able to specify whether messages follow SOAP1.1 or SOAP1.2 protocol.
 * Current implementation uses 1.1 only
 */
public class FullResponseSoapAdapter extends XmlAdapter implements Adapter
{
	/**
     * WSDL URL parameter. OPTIONAL.
     */
    public static final String PARAM_WSDL_URL = "wsdlURL";

    /**
     * Default key to identify PAYLOAD Map within SimulatorPojo's root
     */
    public static final String DEFAULT_PAYLOAD_KEY = "payload";
    
    /**
     * The key corresponding to the full SOAP response for a matched operation
     */
    public static final String RESPONSE_CONTENT_KEY = "responseContent";

	/**
     * Content Type header
     */
    public static final String CONTENT_TYPE = "Content-Type";

	/**
	 *  SOAPAction header required for SOAP version 1.1
	 */
    public static final String SOAP_ACTION = "SOAPAction";

    /**
     * Logger for this class.
     */
    private static Logger logger = LoggerFactory.getLogger(FullResponseSoapAdapter.class);

    /**
     * Available operations defined in the provided WSDL.
     */
    private Map<String, BindingOperation> availableOps = new HashMap<String, BindingOperation>();

    /**
     * Constructor
     */
    public FullResponseSoapAdapter()
    {
        super(false);
    }

    /**
     * Constructor
     *
     * @param bound Configurable bound
     * @param parameters @see #parameters
     */
    public FullResponseSoapAdapter(final int bound, final Map<String, String> parameters)
    {
        super(bound, parameters, false);
    }

    /**
     * @inheritDoc
     * @param exchange Exchange message
     * @return SimulatorPojo object
     * @throws FormatAdapterException if an error occurs
     */
    @Override
    protected SimulatorPojo createSimulatorPojo(final Exchange exchange) throws
            FormatAdapterException
    {
        return createSimulatorPojo(exchange.getIn().getBody(String.class));
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
     * @param pojo The populated SimulatorPojo with the script execution results
     * @param exchange      The Camel exchange
     * @return The String representation of the input data
     * @throws FormatAdapterException If any error occurs
     *
     * @inheritDoc
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected String getString(final SimulatorPojo pojo, final Exchange exchange)
        throws FormatAdapterException
    {
        logger.debug("Attempting to generate SOAP message from SimulatorPojo:\n   ", pojo);
        Map<String, Map> payload = null;

        // --- First, check we got a "payload" in POJO's root.
        if (pojo.getRoot().containsKey(DEFAULT_PAYLOAD_KEY))
        {
        	// --- Grab the PAYLOAD results Map
        	payload = (Map<String, Map>) pojo.getRoot().get(DEFAULT_PAYLOAD_KEY);
        }
        else
        {
        	//because returning payload; in the scenario is the most common case (is there an alternative?)
        	//this branch will probably always execute and the root of the pojo will already be the content of payload.
        	// --- Grab the PAYLOAD results Map
        	payload = (Map<String, Map>) (Object) pojo.getRoot();
        }
        
        exchange.getOut().setHeader(CONTENT_TYPE, SOAPConstants.SOAP_1_1_CONTENT_TYPE);
        //Only needed for SOAP1.1
        exchange.getOut().setHeader(SOAP_ACTION, SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE);
        
        if (payload.isEmpty())
        {
        	return "";
        }
        Map<String, Map<String, Object>> operationMap = payload.values().iterator().next();
        Map<String, Object> portMap = operationMap.values().iterator().next();
        return (String) portMap.get(RESPONSE_CONTENT_KEY);
    }
   
   /**
     *
     * @param soapString The String representation of the SOAP message
     * @return The generated SimulatorPojo
     * @throws FormatAdapterException If any error during the process occurs
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private SimulatorPojo createSimulatorPojo(final String soapString)
        throws FormatAdapterException
    {
        logger.debug("Attempting to generate SimulatorPojo from SOAP content:\n{}", soapString);

        SimulatorPojo pojo = new StructuredSimulatorPojo();

        try
        {
        	MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);

            InputStream is = new ByteArrayInputStream(soapString.getBytes("UTF-8"));
            // TODO - SO WHAT ABOUT THE HEADERS?
            SOAPMessage message = messageFactory.createMessage(null, is);

            // --- So, now we got the SOAP message parsed.
            SOAPBody body = message.getSOAPBody();

            Map<String, Map> payload = (Map<String, Map>) getStructuredChilds(body);

            // --- Check that the passed methods/parameters are WSDL-valid
            validateOperationsAndParameters(payload);
            
            pojo.getRoot().put(DEFAULT_PAYLOAD_KEY, addResponseParametersAndFault(payload));
        }
        catch (SOAPException se)
        {
            String errorMessage = "Unexpected SOAP exception trying to generate SimulatorPojo: ";
            throw new FormatAdapterException(errorMessage, se);
        }
        catch (UnsupportedEncodingException uee)
        {
            String errorMessage = "Unsupported encoding exception: ";
            throw new FormatAdapterException(errorMessage, uee);
        }
        catch (IOException ioe)
        {
            String errorMessage = "Unexpected IO exception: ";
            throw new FormatAdapterException(errorMessage, ioe);
        }

        logger.debug("Finished generating SimulatorPojo from SOAP content");
        return pojo;
    }

    /**
     * Downloads and reads a WSDL from the provided URI
     * @throws ConfigurableException If the WSDL file is not in the provided URI or is wrong
     */
    @SuppressWarnings("unchecked")
    private void getWSDLDefinition() throws ConfigurableException
    {
        try
        {
            WSDLFactory wsdlFactory = WSDLFactory.newInstance();
            WSDLReader wsdlReader = wsdlFactory.newWSDLReader();

            Definition definition = wsdlReader.readWSDL(getParamValue(PARAM_WSDL_URL));
            if (definition == null)
            {
                throw new ConfigurableException(
                        "Definition element is null for WSDL URL: "
                                + getParamValue(PARAM_WSDL_URL));
            }

            getAvailableOperations(definition);
        }
        catch (WSDLException we)
        {
            String errorMsg = "Unexpected WSDL error: ";
            throw new ConfigurableException(errorMsg, we);
        }
    }

    /**
     * Populates the list of available operations and their parts as defined in the provided WSDL
     */
    @SuppressWarnings("unchecked")
    private void getAvailableOperations(Definition definition)
    {
        Map<QName, Binding> bindings = definition.getAllBindings();

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
     * @param payload Map containing SOAP message's payload
     * @return True if payload is valid. False otherwise.
     * @throws FormatAdapterException If any validation fails.
     * @throws SOAPException If any SOAP generation error occurs
     */
    @SuppressWarnings("rawtypes")
    private boolean validateOperationsAndParameters(final Map<String, Map> payload)
        throws FormatAdapterException, SOAPException
    {
        // --- Review all Methods passed in the SOAP message
        for (Map.Entry<String, Map> operationEntry : payload.entrySet())
        {
            String opName = operationEntry.getKey();
            
            if (!availableOps.containsKey(opName))
            {
                // --- If the requested Operation is no available, throw an error
                throw new FormatAdapterException(
                        "The requested service operation is not available in the provided WSDL: "
                                + opName);
            }
        }
        return true;
    }
    
    /**
     * Adds the expected response parts and Fault to the payload Map representation
     * @param payload The payload containing the original SOAP message representation
     * @return Payload including response parts ans Fault
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Map<String, Map> addResponseParametersAndFault(final Map<String, Map> payload)
    {
        // --- Set the response parameters to the invoked method in the payload
        for (Map.Entry<String, Map> methodEntry : payload.entrySet())
        {
            Map<String, Object> methodParams = methodEntry.getValue();

            BindingOperation availableOp = availableOps.get(methodEntry.getKey());

            // --- Get the response parameters and add them to the current method
            Map<String, Part> parts =
                    availableOp.getOperation().getOutput().getMessage().getParts();

            for (Part part : parts.values())
            {
                if (!methodParams.containsKey(part.getName()))
                {
                    methodParams.put(part.getName(), new HashMap());
                }
            }
        }

        return payload;
    }    

    @Override
    protected Map<String, Object> generateClasses(SimulatorPojo pojo)
    		throws FormatAdapterException
    {
    	return pojo.getRoot();
    }
}
