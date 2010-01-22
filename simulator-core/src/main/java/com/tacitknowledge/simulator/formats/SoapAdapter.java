package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.Configurable;
import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.StructuredSimulatorPojo;
import com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

    /**
     * Default key to identify PAYLOAD Map withing SimulatorPojo's root
     */
    public static final String DEFAULT_PAYLOAD_KEY = "payload";

    /**
     * sOAP Fault key
     */
    public static final String FAULT = "fault";

    /**
     * SOAP FaultCode key
     */
    public static final String FAULT_CODE = "faultCode";

    /**
     * SOAP FaultString key
     */
    public static final String FAULT_STRING = "faultString";

    /**
     * SOAP FaultActor key
     */
    public static final String FAULT_ACTOR = "faultActor;";

    /**
     * Logger for this class.
     */
    private static Logger logger = LoggerFactory.getLogger(SoapAdapter.class);

    /**
     * Soap Factory object
     */
    private SOAPFactory soapFactory;
    /**
     * Message factory object
     */
    private MessageFactory messageFactory;

    /**
     * Adapter parameters definition.
     */
    private List<ParameterDefinitionBuilder.ParameterDefinition> parametersList = parameters()
            .add(name(PARAM_WSDL_URL).label("WSDL URL").required());

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
     *  Payload NS
     */
    private String payloadNS;
    /**
     *  Payload NSUri
     */
    private String payloadNSUri;

    /**
     * SOAP message body
     */
    private SOAPMessage soapMessage;

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
    public SoapAdapter(final int bound, final Map<String, String> parameters)
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
    protected SimulatorPojo createSimulatorPojo(final Exchange exchange)
            throws FormatAdapterException
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
     * @return
     * @throws com.tacitknowledge.simulator.FormatAdapterException
     *
     * @inheritDoc
     */
    @Override
    protected String getString(SimulatorPojo pojo, Exchange exchange)
            throws FormatAdapterException
    {
        logger.debug("Attempting to generate SOAP message from SimulatorPojo:\n" + pojo);

        // --- First, check we got a "payload"
        if (!pojo.getRoot().containsKey(DEFAULT_PAYLOAD_KEY))
        {
            throw new FormatAdapterException("Expecting a PAYLOAD key in SimulatorPojo's root.");
        }

        // --- Grab the PAYLOAD results Map
        Map<String, Map> payload = (Map<String, Map>) pojo.getRoot().get(DEFAULT_PAYLOAD_KEY);

        // --- Will need it to get the SOAPMessage as a String
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // --- If we're OK, assemble the SOAP message
        try
        {
            soapFactory = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
            messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

            soapMessage = messageFactory.createMessage();
            SOAPBody soapBody = soapMessage.getSOAPBody();

            // --- First things first. Check if we got a fault code & string...
            Map<String, String> fault = (Map<String, String>) payload.get(FAULT);
            if (fault != null &&
                    !fault.get(FAULT_CODE).isEmpty() &&
                    !fault.get(FAULT_STRING).isEmpty())
            {
                // --- If we do, we'll return a SOAP FAULT instead of a regular payload
                addFaultToResponse(
                        fault.get(FAULT_CODE),
                        fault.get(FAULT_STRING),
                        fault.get(FAULT_ACTOR));
            }
            else
            {
                // --- Validate the result method and parameters
                if (validateOperationsAndParameters(payload))
                {
                    // --- If validation was successful,
                    // add the payload's content to the SOAP body
                    for (Map.Entry<String, Map> payloadItem : payload.entrySet())
                    {
                        String methodName = payloadItem.getKey();
                        Map<String, Object> itemChildren =
                                (Map<String, Object>) payloadItem.getValue();

                        BindingOperation availableOp = availableOps.get(methodName);
                        Map<String, Part> outputParts =
                                (Map<String, Part>)
                                        availableOp.getOperation().getOutput().
                                                getMessage().getParts();

                        SOAPBodyElement elem = soapBody.addBodyElement(
                                getQName(methodName + "Response"));

                        // --- Now add to the element the response parameters
                        for (Map.Entry<String, Object> child : itemChildren.entrySet())
                        {
                            String childName = child.getKey();
                            Object childValue = child.getValue();

                            // --- Add only the response parts defined in the WSDL
                            if (outputParts.containsKey(childName))
                            {
                                if (childValue instanceof Map)
                                {
                                    elem.addChildElement(
                                            getSOAPElementFromMap(
                                                    childName,
                                                    (Map<String, Object>) childValue));
                                }
                                else if (childValue instanceof String)
                                {
                                    elem.addChildElement(
                                            getSOAPElementFromString(
                                                    childName,
                                                    childValue.toString())
                                    );
                                }
                            }
                        }
                    }
                }
            }
            soapMessage.writeTo(outputStream);
        }
        catch(SOAPException se)
        {
            String errorMsg = "Unexpected SOAP exception trying to generate SOAP message: " +
                    se.getMessage();
            logger.error(errorMsg, se);
            throw new FormatAdapterException(errorMsg, se);
        }
        catch(IOException ioe)
        {
            String errorMsg = "Unexpected IO Exception trying to get SOAP message as String: " +
                    ioe.getMessage();
            logger.error(errorMsg, ioe);
            throw new FormatAdapterException(errorMsg, ioe);
        }

        logger.debug("Finished generating SOAP content from SimulatorPojo");
        return outputStream.toString();
    }

    private SOAPElement getSOAPElementFromMap(String elemName, Map<String, Object> items)
            throws SOAPException
    {
        SOAPElement elem = soapFactory.createElement(getQName(elemName));

        for (Map.Entry<String, Object> item : items.entrySet())
        {
            String itemName = item.getKey();
            Object itemValue = item.getValue();

            if (item.getValue() instanceof Map)
            {
                // --- Go down
                elem.addChildElement(
                        getSOAPElementFromMap(
                                itemName,
                                (Map<String, Object>) itemValue));
            }
            else if (item.getValue() instanceof String)
            {
                elem.addChildElement(
                        getSOAPElementFromString(itemName, itemValue.toString()));
            }
        }

        return elem;
    }

    private SOAPElement getSOAPElementFromString(String elemName, String text)
            throws SOAPException
    {
        SOAPElement elem = soapFactory.createElement(getQName(elemName));
        elem.addTextNode(text);

        return elem;
    }

    /**
     *
     * @param o The String representation of the SOAP message
     * @return The generated SimulatorPojo
     * @throws FormatAdapterException If any error during the process occurs
     */
    private SimulatorPojo createSimulatorPojo(String o)
        throws FormatAdapterException
    {
        logger.debug("Attempting to generate SimulatorPojo from SOAP content:\n" + o);

        SimulatorPojo pojo = new StructuredSimulatorPojo();

        try
        {
            messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

            InputStream is = new ByteArrayInputStream(o.getBytes("UTF-8"));
            // TODO - SO WHAT ABOUT THE HEADERS?
            SOAPMessage message = messageFactory.createMessage(null, is);

            // --- So, now we got the SOAP message parsed.
            SOAPBody body = message.getSOAPBody();

            Map<String, Map> payload = (Map<String, Map>) getStructuredChilds(body);

            // --- Check that the passed methods/parameters are WSDL-valid
            validateOperationsAndParameters(payload);

            pojo.getRoot().put(payloadKey, addResponseParametersAndFault(payload));
        }
        catch(SOAPException se)
        {
            String errorMessage = "Unexpected SOAP exception trying to generate SimulatorPojo: " + se.getMessage();
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

        logger.debug("Finished generating SimulatorPojo from SOAP content");
        return pojo;
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

            String targetNS = definition.getTargetNamespace();
            Map<String, String> namespaces = definition.getNamespaces();

            for (Map.Entry<String, String> ns : namespaces.entrySet())
            {
                if (ns.getValue().equals(targetNS))
                {
                    payloadNS = ns.getKey();
                    payloadNSUri = ns.getValue();
                }
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
     * @param payload Map containing SOAP message's payload
     * @throws FormatAdapterException If any validation fails.
     * @throws SOAPException If any SOAP generation error occurs
     */
    private boolean validateOperationsAndParameters(Map<String, Map> payload)
            throws FormatAdapterException, SOAPException
    {
        // --- Review all Methods passed in the SOAP message
        for (Map.Entry<String, Map> operationEntry :
                payload.entrySet())
        {
            String opName = operationEntry.getKey();
            Map<String, Object> opParameters = operationEntry.getValue();
            
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
            for (Part partInOp : partsInAvailableOp.values())
            {
                if (!opParameters.containsKey(partInOp.getName()))
                {
                    String bound = getBound() == Configurable.BOUND_IN ? "inbound" : "outbound";
                    // --- If the defined method part is not in the message, throw an error
                    String errorMsg = "Missing required "+ bound +
                            " parameter (" + partInOp.getName() +
                            ") for method " + opName + " as defined in the provided WSDL";
                    logger.error(errorMsg);

                    // --- If this is an outbound adapter, return a FAULT message in case of
                    // missing params/parts
                    if (getBound() == Configurable.BOUND_OUT)
                    {
                        addFaultToResponse("Sender", errorMsg, null);
                        return false;
                    }
                    throw new FormatAdapterException(errorMsg);
                }
            }
        }
        return true;
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
     * @see com.tacitknowledge.simulator.configuration
     *      .ParameterDefinitionBuilder.ParameterDefinition
     */
    @Override
    public List<List> getParametersList()
    {
        return getParametersDefinitionsAsList(parametersList);
    }

    private QName getQName(String localPart)
    {
        QName qname = new QName(localPart);
        if (this.payloadNS != null)
        {
            qname = new QName(this.payloadNSUri, payloadNS + ":" + localPart);
        }
        return qname;
    }

    private Map<String, Map> addResponseParametersAndFault(Map<String, Map> payload)
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
                methodParams.put(part.getName(), "");
            }
        }

        // --- Add the fault object to the POJO
        Map<String, String> fault = new HashMap<String, String>();
        fault.put(FAULT_CODE, "");
        fault.put(FAULT_STRING, "");
        fault.put(FAULT_ACTOR, "");
        payload.put(FAULT, fault);

        return payload;
    }

    private void addFaultToResponse(String code, String string, String actor)
            throws SOAPException
    {
        SOAPFault fault = soapMessage.getSOAPBody().addFault();
        fault.setFaultCode(new QName(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, code));
        fault.setFaultString(string);
        if (actor != null && !actor.isEmpty())
        {
            fault.setFaultActor(actor);
        }
    }
}
