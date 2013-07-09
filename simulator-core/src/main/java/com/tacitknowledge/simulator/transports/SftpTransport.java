package com.tacitknowledge.simulator.transports;

import java.lang.annotation.Inherited;
import java.util.Map;

import com.tacitknowledge.simulator.BaseConfigurable;
import com.tacitknowledge.simulator.Configurable;
import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.TransportException;

public class SftpTransport extends FtpTransport
{
    /**
     * {@link Inherited}
     */
    SftpTransport(){
        this(new BaseConfigurable());
    }
    
    /**
     * 
     * @param configurable -transport parameters
     */
    public SftpTransport(final Configurable configurable)
    {
        super(configurable);
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
