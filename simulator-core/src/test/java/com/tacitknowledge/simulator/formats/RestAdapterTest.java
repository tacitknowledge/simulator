package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorPojo;
import junit.framework.TestCase;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.DefaultMessage;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Test class for RestAdapterTest
 *
 * @author Raul Huerta (rhuerta@tacitknowledge.com)
 */
public class RestAdapterTest extends TestCase {

    private RestAdapter adapter;

    public void setUp()
    {
        adapter = (RestAdapter) AdapterFactory.getAdapter(FormatConstants.REST);
    }

    public void testCreateSimulatorPojo() throws FormatAdapterException {
        //TODO in progress
    }
   
}