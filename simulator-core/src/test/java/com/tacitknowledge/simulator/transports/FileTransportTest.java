package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.Transport;
import com.tacitknowledge.simulator.TransportException;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * @author galo
 */
public class FileTransportTest extends TestCase
{
    private Map<String, String> params;

    public void setUp()
    {
        params = new HashMap<String, String>();
    }

    public void testGetUriWithoutParams()
    {
        // --- Create a FileTransport with default constructor
        Transport transport = new FileTransport();

        assertEquals("FILE", transport.getType());

        // --- Try to get the URI
        try
        {
            transport.toUriString();
            fail("Transport should not work without required parameters");
        }
        catch (TransportException e)
        {
            // --- That's ok
        }
    }

    public void testGetUriDeletingFile()
    {
        // --- Try to get this URI: file://inbox?delete=true
        params.put(FileTransport.PARAM_DIRECTORY_NAME, "inbox");
        params.put(FileTransport.PARAM_DELETE_FILE, "true");

        // ---
        Transport transport = new FileTransport(params);

        // --- Get the URI
        try
        {
            String uri = transport.toUriString();

            assertTrue("Returned uri isn't as expected: " + uri, uri.indexOf("file://inbox?delete=true") > -1);
        }
        catch (TransportException e)
        {
            fail("Shouldn't be getting an exception here: " + e.getMessage());
        }
    }

    public void testGetUriForCsvFilesOnly()
    {
        // --- Try to get this URI: file://inbox/csv?include=^.*(i)(.csv)
        params.put(FileTransport.PARAM_DIRECTORY_NAME, "inbox/csv");
        params.put(FileTransport.PARAM_FILE_EXTENSION, "csv");

        Transport transport = new FileTransport(params);

        try
        {
            String uri = transport.toUriString();

            assertTrue("Returned uri isn't as expected: " + uri, uri.indexOf("file://inbox/csv?include=^.*(i)(.csv)") > -1);
        }
        catch (TransportException e)
        {
            fail("Shouldn't be getting an exception here: " + e.getMessage());
        }
    }
}
