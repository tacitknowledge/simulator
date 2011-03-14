package com.tacitknowledge.simulator.impl;

import java.io.IOException;
import java.util.List;

import org.apache.camel.Exchange;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.ConversationManager;
import com.tacitknowledge.simulator.ConversationScenario;
import com.tacitknowledge.simulator.ScenarioParsingException;
import com.tacitknowledge.simulator.SimulatorCamelTestSupportBase;
import com.tacitknowledge.simulator.configuration.EventDispatcher;
import com.tacitknowledge.simulator.configuration.SimulatorEventListener;
import com.tacitknowledge.simulator.configuration.loaders.ConversationLoader;
import com.tacitknowledge.simulator.configuration.loaders.ScenarioLoader;

/**
 * Test class for ConversationManagerImpl
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class ConversationManagerImplTest extends SimulatorCamelTestSupportBase
{
    private static final String SCENARIO_PATH = "systems/sys1/conv2/scenario2.scn";

    public static final String CONVERSATION_PATH_SYSTEMS_SYS1_CONV1 = "systems/sys1/conv1";

    public static final String TEST_IMPL_1 = "com.tacitknowledge.simulator.impl.ConversationManagerImplTest$TestEventListenerImpl1";

    public static final String TEST_IMPL_2 = "com.tacitknowledge.simulator.impl.ConversationManagerImplTest$TestEventListenerImpl2";

    public static final String TEST_IMPL_3 = "com.tacitknowledge.simulator.impl.ConversationManagerImplTest$TestEventListenerImpl3";

    ConversationManagerImpl conversationManager;

    /**
     *  Conversation folder path.
     */
    String folderPath;

    /**
     * Scenario file path.
     */
    String scenarioPath;

    @Before
    public void setUp() throws IOException
    {
        conversationManager = new ConversationManagerImpl(null, null)
        {
            public boolean hasDifferencesInConfiguration(Conversation conv1, Conversation conv2)
            {
                return super.hasDifferencesInConfiguration(conv1, conv2);
            }
        };

        folderPath = new String();
        scenarioPath = new String();

        folderPath = new ClassPathResource(CONVERSATION_PATH_SYSTEMS_SYS1_CONV1).getFile()
                .getAbsolutePath();
        scenarioPath = new ClassPathResource(SCENARIO_PATH).getFile().getAbsolutePath();
    }

    @Test
    public void testRegisterListeners()
    {
        ConversationManager manager = new ConversationManagerImpl(null, null);
        manager.registerListeners(System.getProperty("user.dir") + "/src/test/resources/listeners");
        List<SimulatorEventListener> listeners = EventDispatcher.getInstance()
                .getSimulatorEventListeners();
        assertTrue(listeners.size() > 0);
        //need to do this since equals method could be different among implementations
        boolean impl1Found = false;
        boolean impl2Found = false;
        boolean impl3Found = false;

        for (SimulatorEventListener listener : listeners)
        {
            String className = listener.getClass().getName();
            if (className.equals(TEST_IMPL_1))
            {
                impl1Found = true;
            }
            else if (className.equals(TEST_IMPL_2))
            {
                impl2Found = true;
            }
            else if (className.equals(TEST_IMPL_3))
            {
                impl3Found = true;
            }
        }

        assertTrue(impl1Found && impl2Found && impl3Found);
    }

    /**
     * SimulatorEventListener implementation for testing purposes
     */
    public static final class TestEventListenerImpl1 implements SimulatorEventListener
    {

        public TestEventListenerImpl1()
        {}

        public void onNewMessage(Exchange exchange, Conversation conversation)
        {}

        public void onMatchingScenario(Exchange exchange, Conversation conversation)
        {}

        public void onResponseBuilt(Exchange exchange, Conversation conversation)
        {}

        public void onResponseSent(Exchange exchange, Conversation conversation)
        {}
    }

    /**
     * SimulatorEventListener implementation for testing purposes
     */
    public static final class TestEventListenerImpl2 implements SimulatorEventListener
    {

        public void onNewMessage(Exchange exchange, Conversation conversation)
        {}

        public void onMatchingScenario(Exchange exchange, Conversation conversation)
        {}

        public void onResponseBuilt(Exchange exchange, Conversation conversation)
        {}

        public void onResponseSent(Exchange exchange, Conversation conversation)
        {}
    }

    /**
     * SimulatorEventListener implementation for testing purposes
     */
    public static final class TestEventListenerImpl3 implements SimulatorEventListener
    {

        public void onNewMessage(Exchange exchange, Conversation conversation)
        {}

        public void onMatchingScenario(Exchange exchange, Conversation conversation)
        {}

        public void onResponseBuilt(Exchange exchange, Conversation conversation)
        {}

        public void onResponseSent(Exchange exchange, Conversation conversation)
        {}
    }

    /**
     * Test detection of different inbound date.
     */
    @Test
    public void testHasDifferencesInConfigurationForDiferentInboundDate()
    {

        final long date1 = System.currentTimeMillis();
        final long date2 = date1 + 100;
        
        Conversation conversation1 = new ConversationImpl(folderPath, null, null, null, null)
        {
            @Override
            public long getIboundModifiedDate()
            {
                // TODO Auto-generated method stub
                return date1;
            }
        };

        Conversation conversation2 = new ConversationImpl(folderPath, null, null, null, null)
        {
            @Override
            public long getIboundModifiedDate()
            {
                // TODO Auto-generated method stub
                return date2;
            }
        };
        Assert.assertTrue(conversationManager.hasDifferencesInConfiguration(conversation1,
                conversation2));
    }

    /**
     * Test detection of different outbound date
     */
    @Test
    public void testHasDifferencesInConfigurationForDiferentOutboundDate()
    {

        final long date1 = System.currentTimeMillis();
        final long date2 = date1 + 100;
        
        Conversation conversation1 = new ConversationImpl(folderPath, null, null, null, null)
        {
            @Override
            public long getOutboundModifiedDate()
            {
                return date1;
            }
        };

        Conversation conversation2 = new ConversationImpl(folderPath, null, null, null, null)
        {
            @Override
            public long getOutboundModifiedDate()
            {
                return date2;
            }
        };
        Assert.assertTrue(conversationManager.hasDifferencesInConfiguration(conversation1,
                conversation2));
    }

    @Test
    public void testHasDifferencesInConfigurationForDiferentConversationsSize()
    {
        ConversationLoader conversationLoader = new ConversationLoader(new ScenarioLoader());

        Conversation conversation1 = null;
        Conversation conversation2 = null;

        ScenarioLoader scenarioLoader = new ScenarioLoader();
        try
        {
            conversation1 = conversationLoader.parseConversationFromPath(folderPath);
            conversation2 = conversationLoader.parseConversationFromPath(folderPath);
            ConversationScenario conversationScenario = scenarioLoader
                    .parseScenarioFromFile(scenarioPath);
            conversation2.addScenario(conversationScenario);
        }
        catch (IOException e)
        {
            fail(e.getMessage());
        }
        catch (ScenarioParsingException e)
        {
            fail(e.getMessage());
        }

        Assert.assertTrue(conversationManager.hasDifferencesInConfiguration(conversation1,
                conversation2));
    }
}
