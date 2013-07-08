package com.tacitknowledge.simulator.transports;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import com.tacitknowledge.simulator.BaseConfigurable;
import org.junit.Before;
import org.junit.Test;

import com.tacitknowledge.simulator.Transport;

public class SftpTransportTest
{
    private Transport transport;
    private Map<String, String> params;

    @Before
    public void setUp()
    {
        transport = new SftpTransport();
        params = new HashMap<String, String>();
    }
    
    @Test
    public void testGetSftpUri()
    {
        // --- Try to get this URI: sftp://127.0.0.1
        params.put(FtpTransport.PARAM_HOST, "127.0.0.1");
        BaseConfigurable configurable = new BaseConfigurable(params);


        transport = new SftpTransport(configurable);

        try
        {
            String uri = transport.toUriString();

            assertTrue("Returned uri isn't as expected: " + uri,
                uri.indexOf("sftp://127.0.0.1") > -1);
        }
        catch (Exception e)
        {
            fail("Shouldn't be getting an exception here: " + e.getMessage());
        }
    }
}
