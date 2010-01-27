package com.tacitknowledge.simulator.transports;

import java.util.Map;

/**
 * Marker class to be used for soap transport
 *
 * @author Raul Huerta (rhuerta@acitknowledge.com)
 */
public class SoapTransport extends HttpTransport
{
    //Marker class for SOAP

    /**
     * Default Cosntructor
     */
    public SoapTransport()
    {
        super(TransportConstants.SOAP);
    }

    /**
     * Constructor to initialize parameters
     *
     * @param bound      Configurable bound
     * @param parameters - Map of String, String values
     */
    public SoapTransport(final int bound, final Map<String, String> parameters)
    {
        super(bound, TransportConstants.SOAP, parameters);
    }
}
