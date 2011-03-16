package com.tacitknowledge.simulator.transports;

import java.lang.annotation.Inherited;
import java.util.Map;

import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.TransportException;

public class FtpsTransport extends FtpTransport
{
    /**
     * {@link Inherited}
     */
    FtpsTransport(){
        super();
    }
    
    /**
     * 
     * @param bound Inbound or Outbound
     * @param parameters - transport parameters
     */
    public FtpsTransport(final int bound, final Map<String, String> parameters)
    {
        super(bound, parameters);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected String getUriString() throws ConfigurableException, TransportException
    {
        return getUriString(TransportConstants.FTPS.toLowerCase());
    }
}
