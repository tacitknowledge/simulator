package com.tacitknowledge.simulator.transports;

import java.util.Map;

/**
 * Marker class to be used for soap transport
 * @author Raul Huerta (rhuerta@acitknowledge.com)
 */
public class SoapTransport extends HttpTransport {
    //Marker class for SOAP

    /**
     * Default Cosntructor
     */
    public SoapTransport() {
        super(TransportConstants.SOAP);
    }

    /**
     * Constructor to initialize parameters
     * @param parameters - Map of String, String values
     */
    public SoapTransport(Map<String, String> parameters) {
        super(TransportConstants.SOAP, parameters);
    }
}
