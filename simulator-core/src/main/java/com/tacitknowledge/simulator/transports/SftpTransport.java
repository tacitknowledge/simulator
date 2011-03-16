package com.tacitknowledge.simulator.transports;

import java.lang.annotation.Inherited;
import java.util.Map;

import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.TransportException;

public class SftpTransport extends FtpTransport
{
    /**
     * {@link Inherited}
     */
    SftpTransport(){
        super();
    }
    
    /**
     * 
     * @param bound Inbound or Outbound
     * @param parameters - transport parameters
     */
    public SftpTransport(final int bound, final Map<String, String> parameters)
    {
        super(bound, parameters);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected String getUriString() throws ConfigurableException, TransportException
    {
        return getUriString(TransportConstants.SFTP.toLowerCase());
    }
}
