package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.Transport;
import com.tacitknowledge.simulator.TransportException;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * Test class for FtpTransport
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class FtpTransportTest extends TestCase
{
    private Transport transport;
    private Map<String, String> params;

    public void setUp()
    {
        transport = new FtpTransport();
        params = new HashMap<String, String>();
    }

    public void testGetUriWithoutParams()
    {
        assertEquals("FTP", transport.getType());

        // --- Try to get the URI
        try
        {
            transport.toUriString();
            fail("Transport should not work without required parameters");
        }
        catch (Exception e)
        {
            // --- That's ok
        }
    }

    public void testGetSimplestUri()
    {
        // --- Try to get this URI: ftp://127.0.0.1
        params.put(FtpTransport.PARAM_HOST, "127.0.0.1");
        transport.setParameters(params);

        try
        {
            String uri = transport.toUriString();

            assertTrue("Returned uri isn't as expected: " + uri,
                uri.indexOf("ftp://127.0.0.1") > -1);
        }
        catch (Exception e)
        {
            fail("Shouldn't be getting an exception here: " + e.getMessage());
        }
    }

    public void testGetSftpUri()
    {
        // --- Try to get this URI: sftp://127.0.0.1
        params.put(FtpTransport.PARAM_HOST, "127.0.0.1");
        params.put(FtpTransport.PARAM_SFTP, "true");

        transport.setParameters(params);

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

    public void testGetUriWithUserPasswordAndDirectory()
    {
        // --- Try to get this URI: ftp://meandmyself@127.0.0.1:2121/inbox?password=secret
        params.put(FtpTransport.PARAM_HOST, "127.0.0.1");
        params.put(FtpTransport.PARAM_PORT, "2121");
        params.put(FtpTransport.PARAM_USERNAME, "meandmyself");
        params.put(FtpTransport.PARAM_PASSWORD, "secret");
        params.put(FtpTransport.PARAM_DIRECTORY_NAME, "inbox");

        transport.setParameters(params);

        try
        {
            String uri = transport.toUriString();

            assertTrue("Returned uri isn't as expected: " + uri,
                uri.indexOf("ftp://meandmyself@127.0.0.1:2121/inbox?password=secret") > -1);
        }
        catch (Exception e)
        {
            fail("Shouldn't be getting an exception here: " + e.getMessage());
        }
    }

    public void testGetUriWithFileOptionsAndBinary()
    {
        // --- Try to get this URI: ftp://127.0.0.1/inbox?binary=true&include=^.*\\.(csv|CSV)$
        params.put(FtpTransport.PARAM_HOST, "127.0.0.1");
        params.put(FtpTransport.PARAM_DIRECTORY_NAME, "inbox");
        params.put(FtpTransport.PARAM_FILE_EXTENSION, "csv");
        params.put(FtpTransport.PARAM_BINARY, "true");

        transport.setParameters(params);

        try
        {
            String uri = transport.toUriString();

            assertTrue("Returned uri isn't as expected: " + uri,
                uri.indexOf("ftp://127.0.0.1/inbox?binary=true&include=^.*\\.(csv|CSV)$") > -1);
        }
        catch (Exception e)
        {
            fail("Shouldn't be getting an exception here: " + e.getMessage());
        }
    }

    public void testGetUriForFilesWithSomething()
    {
        // --- Try to get this URI: ftp://inbox/csv?include=(.*)(something)(.*)
        params.put(FtpTransport.PARAM_HOST, "127.0.0.1");
        params.put(FtpTransport.PARAM_DIRECTORY_NAME, "inbox/csv");
        params.put(FtpTransport.PARAM_REGEX_FILTER, "(.*)(something)(.*)");

        transport.setParameters(params);

        try
        {
            String uri = transport.toUriString();

            assertTrue(
                "Returned uri isn't as expected: " + uri,
                uri.indexOf("ftp://127.0.0.1/inbox/csv?include=(.*)(something)(.*)") > -1);
        }
        catch (Exception e)
        {
            fail("Shouldn't be getting an exception here: " + e.getMessage());
        }
    }

    public void testGetUriWithLongDelay()
    {
        // --- Try to get this URI: ftp://127.0.0.1/inbox?initialDelay=10000&delay=10000
        params.put(FtpTransport.PARAM_HOST, "127.0.0.1");
        params.put(FtpTransport.PARAM_DIRECTORY_NAME, "inbox");
        params.put(FtpTransport.PARAM_POLLING_INTERVAL, "10000");

        transport.setParameters(params);

        try
        {
            String uri = transport.toUriString();

            assertTrue("Returned uri isn't as expected: " + uri,
                uri.indexOf("ftp://127.0.0.1/inbox?initialDelay=10000&delay=10000") > -1);
        }
        catch (Exception e)
        {
            fail("Shouldn't be getting an exception here: " + e.getMessage());
        }
    }
}
