package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.BaseConfigurable;
import com.tacitknowledge.simulator.Configurable;

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
        this(new BaseConfigurable());
    }

    /**
     * Constructor to initialize parameters
     *
     * @param bound      Configurable bound
     * @param parameters - Map of String, String values
     */
    public SoapTransport(final Configurable configurable)
    {
        super(TransportConstants.SOAP, configurable);
    }
}
