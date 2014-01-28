package com.tacitknowledge.simulator.impl;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.Transport;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by IntelliJ IDEA.
 * User: mshort
 * Date: 7/29/13
 * Time: 11:25 AM
 */
@RunWith(MockitoJUnitRunner.class)
public class ConversationFactoryTest
{
    private ConversationFactory factory;

    private final String conversationPath = StringUtils.EMPTY;
    private Adapter iAdapter;
    private Adapter oAdapter;
    private Transport iTransport;
    private Transport oTransport;

    @Before
    public void setup()
    {
        iAdapter = mock(Adapter.class);
        oAdapter = mock(Adapter.class);
        iTransport = mock(Transport.class);
        oTransport = mock(Transport.class);

        factory = spy(new ConversationFactory());
    }

    @Test
    public void shouldCreateConversation()
    {
        doReturn(Boolean.TRUE).when(factory).allParamsProvided(iAdapter, oAdapter, iTransport, oTransport);

        final Conversation conversation = factory.createConversation(conversationPath, iTransport,
                oTransport, iAdapter, oAdapter);

        assertSame(conversationPath, conversation.getId());
        assertSame(iAdapter, conversation.getInboundAdapter());
        assertSame(oAdapter, conversation.getOutboundAdapter());
        assertSame(iTransport, conversation.getInboundTransport());
        assertSame(oTransport, conversation.getOutboundTransport());
    }

    @Test
    public void shouldThrowExceptionWhenConversationIsCreated()
    {
        final String errorMessage = "some message";

        doReturn(Boolean.FALSE).when(factory).allParamsProvided(iAdapter, oAdapter, iTransport, oTransport);
        doReturn(errorMessage).when(factory).getErrorMessage(iAdapter, oAdapter, iTransport, oTransport);

        try
        {
            final Conversation conversation = factory.createConversation(conversationPath, iTransport,
                    oTransport, iAdapter, oAdapter);
            fail("should throw exception");
        }
        catch (IllegalArgumentException ex)
        {
            assertSame(errorMessage, ex.getMessage());
        }
    }

    @Test
    public void shouldDetectLackOfInboundAdapter()
    {
        assertFalse(factory.allParamsProvided(null, oAdapter, iTransport, oTransport));
    }

    @Test
    public void shouldDetectLackOfOutboundAdapter()
    {
        assertFalse(factory.allParamsProvided(iAdapter, null, iTransport, oTransport));
    }

    @Test
    public void shouldDetectLackOfInboundTransport()
    {
        assertFalse(factory.allParamsProvided(iAdapter, oAdapter, null, oTransport));
    }

    @Test
    public void shouldDetectLackOfOutboundTransport()
    {
        assertFalse(factory.allParamsProvided(iAdapter, oAdapter, iTransport, null));
    }

    @Test
    public void shouldValidateAllParams()
    {
        assertTrue(factory.allParamsProvided(iAdapter, oAdapter, iTransport, oTransport));
    }
}
