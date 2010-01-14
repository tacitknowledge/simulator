package com.tacitknowledge.simulator.transports;

import java.util.Map;

/**
 * Marker class to be used for rest transport
 * @author Raul Huerta (rhuerta@acitknowledge.com)
 */
public class RestTransport extends HttpTransport{
    //Marker class for REST


    /**
     * Default Cosntructor
     */
    public RestTransport() {
        super(TransportConstants.REST);
    }


    /**
     * Constructor to initialize parameters
     * @param parameters - Map of String, String values
     */
    public RestTransport(Map<String, String> parameters) {
        super(TransportConstants.REST, parameters);
    }
}
