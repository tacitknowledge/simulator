package com.tacitknowledge.simulator;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class ConversationManagerImplTest
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
    public void testCreateConversation() throws UnsupportedFormatException
    {
        ConversationManager manager = new ConversationManagerImpl();

        Conversation conversation = manager.createConversation(in, out, FormatConstants.JSON, FormatConstants.XML);
        assertNotNull(conversation);
        assertNotNull(conversation.getEntryTransport());
        assertNotNull(conversation.getExitTransport());
    }

    @Test
    public void testCreateConversationWithWrongFormat()
    {
        ConversationManager manager = new ConversationManagerImpl();

        Conversation conversation = null;
        try
        {
            conversation = manager.createConversation(in, out, "WTF?", FormatConstants.XML);
            fail();
        }
        catch (UnsupportedFormatException e)
        {
            //everything is ok.
        }
        assertNull(conversation);

    }
//    @Test
//    public void testCreateConversationScenario() {
//         fail();
//    }
//
//    @Test
//    public void testActivate() {
//        fail();
//    }
//
//    @Test
//    public void testDeactivate() {
//        fail();
//    }
}
