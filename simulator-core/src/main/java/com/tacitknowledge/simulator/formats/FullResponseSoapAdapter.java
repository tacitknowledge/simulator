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

import com.tacitknowledge.simulator.*;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is based on the logic in SoapAdapter with the difference that the full response is expected
 * to be given in the scenario rather than being built in this class.
 * This provides full flexibility and direct handling of complex namespaces that can occur in the elements.
 * 
 * TODO:
 * Add configuration to be able to specify whether messages follow SOAP1.1 or SOAP1.2 protocol.
 * Current implementation uses 1.1 only
 */
public class FullResponseSoapAdapter extends SoapAdapter implements Adapter
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
     * Constructor
     */
    public FullResponseSoapAdapter()
    {
        this(new BaseConfigurable());
    }

    /**
     * Constructor
     *
     * @param configurable Configurable bound
     */
    public FullResponseSoapAdapter(Configurable configurable) {
        super(configurable, false);
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
        initWSDL();
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
    public void validateParameters() throws ConfigurableException
    {
        if (configuration.getParamValue(PARAM_WSDL_URL) == null)
        {
            throw new ConfigurableException("WSDL URL parameter is required");
        }
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
    protected String getConversationResponseAsString(final SimulatorPojo pojo, final Exchange exchange)
        throws FormatAdapterException
    {
        logger.debug("Attempting to generate SOAP message from SimulatorPojo:\n   ", pojo);
        initWSDL();
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
        /*
        todo - mws and rusnac - this commented out and replaced code seemed buggy to mws,
        todo -    because some of the maps had multiple keys. needs review
        todo - mws and rusnac - I can see the nesting metnioned in Andy's email, however.  Set a breakpoint
                todo - and run the FullResponseSoapTransportIntegrationTest.java
        Map<String, Map<String, Map<String, Object>>> operationMap = payload.values().iterator().next();
        Map<String, Map<String, Object>> portMap = operationMap.values().iterator().next();
        return (String) portMap.values().iterator().next().get(RESPONSE_CONTENT_KEY);

        todo - mws - comment on the above. I set up a success and fault test.  using the operation map seems to
        todo -     handle things properly.
         */
        final String soapOperationKey = payload.keySet().iterator().next();
        //this returns a Map with the SOAP operation as its key.  For the unit test, key is GetBalance
        Map<String, Map<String, Object>> operationMap = payload.get(soapOperationKey);
        //In the unit test, we get 3 entries in the operationMap.
        Map responseMap = operationMap.get(findResponsePartNameFromOperations(soapOperationKey));
        //now get the response content
        return (String) responseMap.get(RESPONSE_CONTENT_KEY);
    }

    private String findResponsePartNameFromOperations(String operationKey) {
        String result = null;
        BindingOperation availableOp = availableOps.get(operationKey);
        //TODO - mws - can there ever be more than one part in our response? Its "response" even for faults
        Map<String, Part> parts =
                    availableOp.getOperation().getOutput().getMessage().getParts();
        for (Part part : parts.values()) {
            result = part.getName();
        }
        return result;
    }

    /*
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


     */
   
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
