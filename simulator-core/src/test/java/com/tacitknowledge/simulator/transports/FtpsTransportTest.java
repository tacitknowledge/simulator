package com.tacitknowledge.simulator.transports;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import com.tacitknowledge.simulator.BaseConfigurable;
import org.junit.Before;
import org.junit.Test;

import com.tacitknowledge.simulator.Transport;

public class FtpsTransportTest
{
    private static final String FTPS_URI_PREFIX = "ftps://127.0.0.1";

    private static final String LOCALHOST_IP = "127.0.0.1";

    private static final String FAILED_BAD_VALIDATION_MESSAGE = "Validation with incomplete parameters should throw an exception";

    private static final String PASSWORD = "password";

    private static final String KEY_FILE_NAME = "filename.jks";

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
        params.put(FtpTransport.PARAM_HOST, LOCALHOST_IP);

        transport = new FtpsTransport(new BaseConfigurable(params));

        try
        {
            String uri = transport.toUriString();

            assertTrue("Returned uri isn't as expected: " + uri, uri.indexOf(FTPS_URI_PREFIX) > -1);
        }
        catch (Exception e)
        {
            fail("Shouldn't be getting an exception here: " + e.getMessage());
        }
    }

    @Test
    public void testGoodValidation()
    {
        // --- Try to get this URI: sftp://127.0.0.1
        params.put(FtpTransport.PARAM_HOST, LOCALHOST_IP);
        params.put(FtpsTransport.KEY_STORE_FILE, KEY_FILE_NAME);
        params.put(FtpsTransport.KEY_STORE_PASSWORD, PASSWORD);
        params.put(FtpsTransport.KEY_STORE_KEY_PASSWORD, PASSWORD);

        transport = new FtpsTransport(new BaseConfigurable(params));


        try
        {
            String uri = transport.toUriString();

            assertTrue(uri.indexOf(FtpsTransport.FTP_CLIENT_KEY_STORE_FILE) > -1);
        }
        catch (Exception e)
        {
            fail("Shouldn't be getting an exception here: " + e.getMessage());
        }
    }

    @Test
    public void testBadValidation()
    {
        // --- Try to get this URI: sftp://127.0.0.1
        params.put(FtpTransport.PARAM_HOST, LOCALHOST_IP);
        params.put(FtpsTransport.KEY_STORE_FILE, KEY_FILE_NAME);
        params.put(FtpsTransport.KEY_STORE_PASSWORD, PASSWORD);

        transport = new FtpsTransport(new BaseConfigurable(params));

        try
        {
            transport.toUriString();
            fail(FAILED_BAD_VALIDATION_MESSAGE);
        }
        catch (Exception e)
        {
            assertTrue(e.getMessage().startsWith(FtpsTransport.REQUIRED_PARAMETERS_MESSAGE));
        }

    }
}
