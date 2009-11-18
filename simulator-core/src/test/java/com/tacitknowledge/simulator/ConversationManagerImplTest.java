package com.tacitknowledge.simulator;

import junit.framework.TestCase;
import org.junit.Test;

import com.tacitknowledge.simulator.camel.RouteManagerImpl;
import com.tacitknowledge.simulator.formats.FormatConstants;
import com.tacitknowledge.simulator.impl.ConversationManagerImpl;

import java.util.List;

/**
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class ConversationManagerImplTest extends TestCase
{
    private Transport in = new Transport()
    {
        public String getType()
        {
            return null;
        }

        public String toUriString()
        {
            return null;
        }
    };
    private Transport out = new Transport()
    {
        public String getType()
        {
            return null;
        }

        public String toUriString()
        {
            return null;
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

        Conversation conversation = manager.createConversation(null, in, out, FormatConstants.JSON, FormatConstants.XML);
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
            conversation = manager.createConversation(null, in, out, "WTF?", FormatConstants.XML);
            fail();
        }
        catch (SimulatorException e)
        {
            //everything is ok.
        }
        assertNull(conversation);

    }
}
