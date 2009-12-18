package com.tacitknowledge.simulator.impl;

import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.ConversationManager;
import com.tacitknowledge.simulator.ConversationNotFoundException;
import com.tacitknowledge.simulator.ConversationScenario;
import com.tacitknowledge.simulator.RouteManager;
import com.tacitknowledge.simulator.SimulatorCamelTestSupportBase;
import com.tacitknowledge.simulator.SimulatorException;
import com.tacitknowledge.simulator.camel.RouteManagerImpl;
import com.tacitknowledge.simulator.formats.AdapterFactory;
import com.tacitknowledge.simulator.formats.FormatConstants;
import org.junit.Test;

import java.util.List;

/**
 * Test class for ConversationManagerImpl
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class ConversationManagerImplTest extends SimulatorCamelTestSupportBase
{

    @Test
    public void testGetCsvFormatParameters()
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

        Conversation conversation = manager.createConversation(null, "testCreateConversation", inTransport, outTransport, AdapterFactory.getAdapter(FormatConstants.JSON), AdapterFactory.getAdapter(FormatConstants.JSON), "");
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
            conversation = manager.createConversation(null, "testCreateConversationWithWrongFormat", inTransport, outTransport, AdapterFactory.getAdapter("WTF?"), AdapterFactory.getAdapter("WTF?"), "");
            fail();
        }
        catch (SimulatorException e)
        {
            //everything is ok.
        }
        assertNull(conversation);

    }

    @Test
    public void testIsActiveConversationNotFound() throws SimulatorException
    {
        ConversationManager manager = new ConversationManagerImpl();
        assertFalse(manager.isActive(1234));
    }


    @Test
    public void testIsActive() throws SimulatorException, ConversationNotFoundException
    {
        RouteManager routeManager = new RouteManagerImpl();
        ConversationManager manager = new ConversationManagerImpl(routeManager);

        Conversation conversation = null;
        conversation = manager.createConversation(1, "testIsActive", inTransport, outTransport, AdapterFactory.getAdapter(FormatConstants.PLAIN_TEXT), AdapterFactory.getAdapter(FormatConstants.PLAIN_TEXT), "");
        assertFalse(manager.isActive(conversation.getId()));
        manager.activate(conversation.getId());
        assertTrue(manager.isActive(conversation.getId()));
    }


    @Test
    public void testCreateOrUpdateScenarioConversationDoesntExits() throws SimulatorException, ConversationNotFoundException
    {
        RouteManager routeManager = new RouteManagerImpl();
        ConversationManager manager = new ConversationManagerImpl(routeManager);

        Conversation conversation = manager.createConversation(1, "testCreateOrUpdateScenarioConversationDoesntExits", inTransport, outTransport, AdapterFactory.getAdapter(FormatConstants.PLAIN_TEXT), AdapterFactory.getAdapter(FormatConstants.PLAIN_TEXT), "");
        ConversationScenario scenario = manager.createOrUpdateConversationScenario(2, 2, "javascript", "true", "2+2");
        assertNull(scenario);
    }


    @Test
    public void testCreateScenario() throws SimulatorException, ConversationNotFoundException
    {
        RouteManager routeManager = new RouteManagerImpl();
        ConversationManager manager = new ConversationManagerImpl(routeManager);

        Conversation conversation = manager.createConversation(1, "testCreateScenario", inTransport, outTransport, AdapterFactory.getAdapter(FormatConstants.PLAIN_TEXT), AdapterFactory.getAdapter(FormatConstants.PLAIN_TEXT), "defaultScenario");
        ConversationScenario scenario = manager.createOrUpdateConversationScenario(1, 2, "javascript", "true", "2+2");
        assertNotNull(scenario);
        assertEquals("javascript", scenario.getScriptLanguage());
        assertEquals("true", scenario.getCriteriaScript());
        assertEquals("defaultScenario\n2+2", scenario.getTransformationScript());


        ConversationScenario scenario1 = manager.createOrUpdateConversationScenario(1, 2, "ruby", "ttttrue", "2+2+2");
        assertSame(scenario, scenario1);

        assertEquals("ruby", scenario.getScriptLanguage());
        assertEquals("ttttrue", scenario.getCriteriaScript());
        assertEquals("defaultScenario\n2+2+2", scenario.getTransformationScript());

    }

    @Test
    public void testDeleteConversation() throws SimulatorException, ConversationNotFoundException
    {
        RouteManager routeManager = new RouteManagerImpl();
        ConversationManager manager = new ConversationManagerImpl(routeManager);

        Conversation conversation = manager.createConversation(1, "testDeleteConversation", inTransport, outTransport, AdapterFactory.getAdapter(FormatConstants.PLAIN_TEXT), AdapterFactory.getAdapter(FormatConstants.PLAIN_TEXT), "");
        ConversationScenario scenario = manager.createOrUpdateConversationScenario(1, 2, "javascript", "true", "2+2");
        assertNotNull(scenario);

        manager.activate(1);
        manager.deleteConversation(1);
        assertTrue(!manager.isActive(1));
    }

    @Test
    public void testDelete() throws SimulatorException, ConversationNotFoundException
    {
        RouteManager routeManager = new RouteManagerImpl();
        ConversationManager manager = new ConversationManagerImpl(routeManager);
        manager.deleteConversation(1234);
    }

    @Test
    public void testConversationExists() throws SimulatorException, ConversationNotFoundException
    {
        RouteManager routeManager = new RouteManagerImpl();
        ConversationManager manager = new ConversationManagerImpl(routeManager);
        assertFalse(manager.conversationExists(1));
        Conversation conversation = manager.createConversation(1, "testConversationExists", inTransport, outTransport, AdapterFactory.getAdapter(FormatConstants.PLAIN_TEXT), AdapterFactory.getAdapter(FormatConstants.PLAIN_TEXT), "");
        assertTrue(manager.conversationExists(1));
        manager.activate(1);
        assertTrue(manager.conversationExists(1));
        manager.deactivate(1);
        assertFalse(manager.conversationExists(1));
    }


    @Test
    public void testDefaultScenarioWasExecuted() throws SimulatorException, ConversationNotFoundException, InterruptedException
    {
        ConversationManager manager = new ConversationManagerImpl(routeManager);

        Conversation conversation = manager.createConversation(1, "testDefaultScenarioWasExecuted", inTransport, outTransport, AdapterFactory.getAdapter(FormatConstants.PLAIN_TEXT), AdapterFactory.getAdapter(FormatConstants.PLAIN_TEXT), "var testVar=123");
        ConversationScenario scenario = manager.createOrUpdateConversationScenario(1, 2, "javascript", "true",
            "testVar=testVar+1\n" +
                "testVar");

        manager.activate(1);

        sendMessage("124.0");

        manager.deactivate(1);
        resultEndpoint.assertIsSatisfied();
        assertNotNull(scenario);
    }


}


