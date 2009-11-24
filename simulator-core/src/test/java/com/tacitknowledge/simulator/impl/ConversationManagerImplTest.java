package com.tacitknowledge.simulator.impl;

import com.tacitknowledge.simulator.*;
import com.tacitknowledge.simulator.camel.RouteManagerImpl;
import com.tacitknowledge.simulator.formats.AdapterFactory;
import com.tacitknowledge.simulator.formats.FormatConstants;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class ConversationManagerImplTest extends TestCase
{
    private Transport in = new Transport()
    {
        public String getType()
        {
            return "sdf";
        }

        public String toUriString()
        {
            return "direct:start";
        }

        public List<List> getParametersList()
        {
            return null;
        }

        public void setParameters(Map<String, String> parameters)
        {

        }
    };
    private Transport out = new Transport()
    {
        public String getType()
        {
            return "dfdfdfdfdf";
        }

        public String toUriString()
        {
            return "mock:result";
        }

        public List<List> getParametersList()
        {
            return null;
        }

        public void setParameters(Map<String, String> parameters)
        {

        }
    };

    @Test
    public void testGetXmlFormatParameters()
    {
        RouteManager routeManager = new RouteManagerImpl();
        ConversationManager manager = new ConversationManagerImpl(routeManager);

        List<List> params = manager.getAdapterParameters(FormatConstants.CSV);
        assertEquals(4, params.size());
    }

    @Test
    public void testCreateConversation() throws SimulatorException
    {
        RouteManager routeManager = new RouteManagerImpl();
        ConversationManager manager = new ConversationManagerImpl(routeManager);

        Conversation conversation = manager.createConversation(null, in, out,  AdapterFactory.getAdapter(FormatConstants.JSON), AdapterFactory.getAdapter(FormatConstants.JSON));
        assertNotNull(conversation);
        assertNotNull(conversation.getInboundTransport());
        assertNotNull(conversation.getOutboundTransport());
    }

    @Test
    public void testCreateConversationWithWrongFormat()
    {
        RouteManager routeManager = new RouteManagerImpl();
        ConversationManager manager = new ConversationManagerImpl(routeManager);

        Conversation conversation = null;
        try
        {
            conversation = manager.createConversation(null, in, out, AdapterFactory.getAdapter("WTF?"), AdapterFactory.getAdapter("WTF?"));
            fail();
        }
        catch (SimulatorException e)
        {
            //everything is ok.
        }
        assertNull(conversation);

    }

    @Test
    public void testIsActiveConversationNotFound() throws SimulatorException {
        ConversationManager manager = new ConversationManagerImpl();
        assertFalse(manager.isActive(1234));
    }


    @Test
    public void testIsActive() throws SimulatorException, ConversationNotFoundException {
        RouteManager routeManager = new RouteManagerImpl();
        ConversationManager manager = new ConversationManagerImpl(routeManager);

        Conversation conversation = null;
        conversation = manager.createConversation(1, in, out, AdapterFactory.getAdapter(FormatConstants.PLAIN_TEXT), AdapterFactory.getAdapter(FormatConstants.PLAIN_TEXT));
        assertFalse(manager.isActive(conversation.getId()));
        manager.activate(conversation.getId());
        assertTrue(manager.isActive(conversation.getId()));
    }

}
