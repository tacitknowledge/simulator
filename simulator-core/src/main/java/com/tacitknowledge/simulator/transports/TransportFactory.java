package com.tacitknowledge.simulator.transports;

import java.util.Properties;

import com.tacitknowledge.simulator.BaseConfigurable;
import com.tacitknowledge.simulator.ConfigurationUtil;
import com.tacitknowledge.simulator.Transport;

public class TransportFactory
{
    public static Transport createTransport(int bound, String type, Properties properties)
    {
        if (TransportConstants.FILE.equals(type))
        {
            return new FileTransport(new BaseConfigurable(bound, ConfigurationUtil.getPropertiesMap(properties)));
        }

        if (TransportConstants.FTP.equals(type))
        {
            return new FtpTransport(new BaseConfigurable(bound, ConfigurationUtil.getPropertiesMap(properties)));
        }

        if (TransportConstants.SFTP.equals(type))
        {
            return new SftpTransport(new BaseConfigurable(bound, ConfigurationUtil.getPropertiesMap(properties)));
        }
        
        if (TransportConstants.FTPS.equals(type))
        {
            return new FtpsTransport(new BaseConfigurable(bound, ConfigurationUtil.getPropertiesMap(properties)));
        }
        
        if (TransportConstants.JMS.equals(type))
        {
            return new JmsTransport(new BaseConfigurable(bound, ConfigurationUtil.getPropertiesMap(properties)));
        }

        if (TransportConstants.REST.equals(type))
        {
            return new RestTransport(new BaseConfigurable(bound, ConfigurationUtil.getPropertiesMap(properties)));
        }

        if (TransportConstants.SOAP.equals(type))
        {
            return new SoapTransport(new BaseConfigurable(bound, ConfigurationUtil.getPropertiesMap(properties)));
        }

        if(TransportConstants.HTTP.equals(type))
        {
            return new HttpTransport(TransportConstants.HTTP,
                    new BaseConfigurable(bound, ConfigurationUtil.getPropertiesMap(properties)));
        }
        return null;
    }
}
