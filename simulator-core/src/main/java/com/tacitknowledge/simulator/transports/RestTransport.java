package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.BaseConfigurable;
import com.tacitknowledge.simulator.Configurable;

import java.util.Map;

/**
 * Marker class to be used for rest transport
 *
 * @author Raul Huerta (rhuerta@acitknowledge.com)
 */
public class RestTransport extends HttpTransport
{
    //Marker class for REST

    /**
     * Default Cosntructor
     */
    public RestTransport()
    {
        this(new BaseConfigurable());
    }


    /**
     * Constructor to initialize parameters
     *
     * @param bound      Configurable bound
     * @param parameters - Map of String, String values
     */
    public RestTransport(final Configurable configurable)
    {
        super(TransportConstants.REST, configurable);
    }
}
