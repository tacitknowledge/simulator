package com.tacitknowledge.simulator.transports;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.tacitknowledge.simulator.Transport;

public class FtpsTransportTest
{
    private Transport transport;
    private Map<String, String> params;

    @Before
    public void setUp()
    {
        transport = new FtpsTransport();
        params = new HashMap<String, String>();
    }
    
    @Test
    public void testGetFftspUri()
    {
        // --- Try to get this URI: sftp://127.0.0.1
        params.put(FtpTransport.PARAM_HOST, "127.0.0.1");

        transport.setParameters(params);

        try
        {
            String uri = transport.toUriString();

            assertTrue("Returned uri isn't as expected: " + uri,
                uri.indexOf("ftps://127.0.0.1") > -1);
        }
        catch (Exception e)
        {
            fail("Shouldn't be getting an exception here: " + e.getMessage());
        }
    }
}
