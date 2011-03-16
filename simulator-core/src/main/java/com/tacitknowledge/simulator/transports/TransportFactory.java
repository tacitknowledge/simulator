package com.tacitknowledge.simulator.transports;

import java.util.Properties;

import com.tacitknowledge.simulator.ConfigurationUtil;
import com.tacitknowledge.simulator.Transport;

public class TransportFactory
{
    public static Transport createTransport(int bound, String type, Properties properties)
    {
        // TODO: Need to refactor transport types to have default constructor and 
        // replace IFs with a get from map
        if (TransportConstants.FILE.equals(type))
        {
            return new FileTransport(bound, ConfigurationUtil.getPropertiesMap(properties));
        }

        if (TransportConstants.FTP.equals(type))
        {
            return new FtpTransport(bound, ConfigurationUtil.getPropertiesMap(properties));
        }

        if (TransportConstants.SFTP.equals(type))
        {
            return new SftpTransport(bound, ConfigurationUtil.getPropertiesMap(properties));
        }
        
        if (TransportConstants.FTPS.equals(type))
        {
            return new FtpsTransport(bound, ConfigurationUtil.getPropertiesMap(properties));
        }
        
        if (TransportConstants.JMS.equals(type))
        {
            return new JmsTransport(bound, ConfigurationUtil.getPropertiesMap(properties));
        }

        if (TransportConstants.REST.equals(type))
        {
            return new RestTransport(bound, ConfigurationUtil.getPropertiesMap(properties));
        }

        if (TransportConstants.SOAP.equals(type))
        {
            return new SoapTransport(bound, ConfigurationUtil.getPropertiesMap(properties));
        }

        return null;
    }
}
