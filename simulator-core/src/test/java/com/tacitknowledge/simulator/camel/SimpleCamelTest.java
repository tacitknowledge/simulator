package com.tacitknowledge.simulator.camel;

import com.tacitknowledge.simulator.BaseConfigurable;
import com.tacitknowledge.simulator.TestHelper;
import com.tacitknowledge.simulator.Transport;
import com.tacitknowledge.simulator.transports.FileTransport;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author galo
 */
public class SimpleCamelTest
{
    public static final String INBOX = "src/main/resources/inbox";
    public static final String OUTBOX = "src/main/resources/outbox";
    public static final String FILE_NAME = "another.xml";

    RouteBuilder builder;

    @Before
    public void setUp()
    {
        builder = new RouteBuilder()
        {
            public void configure()
            {
            }
        };
    }

    @Test
    public void testSimpleFileRoute()
    {
        CamelContext context = new DefaultCamelContext();

        Map<String, String> it_params = new HashMap<String, String>();
        it_params.put(FileTransport.PARAM_DIRECTORY_NAME,
            INBOX);
        it_params.put(FileTransport.PARAM_FILE_EXTENSION, "xml");
        final Transport in_t = new FileTransport(new BaseConfigurable(it_params));

        // --- Out transport & format
        Map<String, String> ot_params = new HashMap<String, String>();
        ot_params.put(FileTransport.PARAM_DIRECTORY_NAME, OUTBOX);
        //ot_params.put(FileTransport.PARAM_FILE_NAME, "regex_result.xml");
        final Transport out_t = new FileTransport(new BaseConfigurable(ot_params));

        try
        {
            context.start();

            System.out.println(in_t.toUriString());
            System.out.println(out_t.toUriString());

            RouteDefinition def = builder.from(in_t.toUriString());
            def.to(out_t.toUriString());

            context.addRoutes(builder);

            File orig = new File(TestHelper.ORIGINAL_FILES_PATH + FILE_NAME);
            assertTrue(orig.exists());

            // --- Don't assert on these, rather just delete them
            File copy = new File(INBOX + "/" + FILE_NAME);
            if (copy.exists())
            {
                copy.delete();
            }
            File result = new File(OUTBOX + "/" + FILE_NAME);
            if (result.exists())
            {
                copy.delete();
            }

            TestHelper.copyFile(orig, copy);

            // --- Wait 5 seconds
            Thread.sleep(10 * 1000);

            // --- Check that we got the expected result file
            assertTrue(result.exists());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }
}
