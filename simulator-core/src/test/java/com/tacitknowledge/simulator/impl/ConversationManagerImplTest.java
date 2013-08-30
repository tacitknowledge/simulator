package com.tacitknowledge.simulator.impl;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.camel.Exchange;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.ConversationManager;
import com.tacitknowledge.simulator.Scenario;
import com.tacitknowledge.simulator.RouteManager;
import com.tacitknowledge.simulator.ScenarioParsingException;
import com.tacitknowledge.simulator.SimulatorCamelTestSupportBase;
import com.tacitknowledge.simulator.Transport;
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

    private static final String CONV_PATH = "root/path";

    private ConversationManagerImpl conversationManager;

    /**
     *  Conversation folder path.
     */
    private String folderPath;

    /**
     * Scenario file path.
     */
    private String scenarioPath;
    
    private ScenarioFactory scenarioFactory;
    
    private ConversationFactory conversationFactory;

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
        
        scenarioFactory = new ScenarioFactory();
        conversationFactory = new ConversationFactory();
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

    @Test
    public void testFirstLoadConversations() throws Exception {
        final Conversation conversation = createConversation();

        ConversationLoader conversationLoader = createConversationsLoader(conversation);

        RouteManager rManager = mock(RouteManager.class);
        ConversationManager cManager = new ConversationManagerImpl(rManager, conversationLoader);

        cManager.loadConversations("root");

        verify(rManager).activate(eq(conversation));
    }

    @Test
    public void testTwiceLoadConversations() throws Exception {
        final Conversation conversation = createConversation();

        ConversationLoader conversationLoader = createConversationsLoader(conversation);

        RouteManager rManager = mock(RouteManager.class);
        ConversationManager cManager = new ConversationManagerImpl(rManager, conversationLoader);

        cManager.loadConversations("root");
        verify(rManager).activate(eq(conversation));
        reset(rManager);

        cManager.loadConversations("root");
        verify(rManager, never()).activate(eq(conversation));
        verify(rManager, never()).deactivate(eq(conversation));
        verify(rManager, never()).delete(eq(conversation));
    }

    @Test
    public void testChangedConversation() throws Exception {
        final Conversation conv1 = createConversation();
        final Conversation conv2 = createConversation();

        ConversationLoader loader = mock(ConversationLoader.class);

        createConversationsLoader(loader, conv1);

        RouteManager rManager = mock(RouteManager.class);
        ConversationManager cManager = new ConversationManagerImpl(rManager,loader);

        cManager.loadConversations("root");
        verify(rManager).activate(eq(conv1));
        reset(rManager);
        createConversationsLoader(loader, conv2);
        
        cManager.loadConversations("root");
        verify(rManager).deactivate(eq(conv1));
        verify(rManager).activate(eq(conv2));
    }

    @Test
    public void testDeletedConversation() throws Exception {
        final Conversation conv1 = createConversation();

        ConversationLoader loader = mock(ConversationLoader.class);

        createConversationsLoader(loader, conv1);

        RouteManager rManager = mock(RouteManager.class);
        ConversationManager cManager = new ConversationManagerImpl(rManager,loader);

        cManager.loadConversations("root");
        verify(rManager).activate(eq(conv1));
        reset(rManager);
        createConversationsLoader(loader, null);

        cManager.loadConversations("root");
        verify(rManager).delete(eq(conv1));
    }

    @Test
    public void testDeletedConversation2() throws Exception {
        final Conversation conv1 = createConversation();
        final Conversation conv2 = createConversation("conv1");

        ConversationLoader loader = mock(ConversationLoader.class);

        createConversationsLoader(loader, conv1);

        RouteManager rManager = mock(RouteManager.class);
        ConversationManager cManager = new ConversationManagerImpl(rManager,loader);

        cManager.loadConversations("root");
        verify(rManager).activate(eq(conv1));
        reset(rManager);
        createConversationsLoader(loader, conv2);

        cManager.loadConversations("root");
        verify(rManager).delete(eq(conv1));
    }

    private Conversation createConversation() {
        return createConversation(CONV_PATH);
    }

    private Conversation createConversation(String path) {
        Transport inputTransport = mock(Transport.class);
        Transport outputTransport = mock(Transport.class);
        Adapter inAdapter = mock(Adapter.class);
        Adapter outAdapter = mock(Adapter.class);
        Conversation conversation = mock(Conversation.class);

        when(conversation.getId()).thenReturn(path);
        when(conversation.getInboundTransport()).thenReturn(inputTransport);
        when(conversation.getOutboundTransport()).thenReturn(outputTransport);
        when(conversation.getInboundAdapter()).thenReturn(inAdapter);
        when(conversation.getOutboundAdapter()).thenReturn(outAdapter);

        long inboundDate = System.currentTimeMillis();
        long outboundDate = System.currentTimeMillis();
        when(conversation.getIboundModifiedDate()).thenReturn(inboundDate);
        when(conversation.getOutboundModifiedDate()).thenReturn(outboundDate);

        return conversation;
    }

    private ConversationLoader createConversationsLoader(final Conversation conversation) throws IOException {
        return createConversationsLoader(mock(ConversationLoader.class), conversation);
    }

    private ConversationLoader createConversationsLoader(ConversationLoader mockLoader, final Conversation conversation) throws IOException {
        Map<String, Conversation> answer = new HashMap<String, Conversation>();
        if (conversation != null) {
            answer.put(conversation.getId(), conversation);
        }
        when(mockLoader.loadAllConversationsInDirectory(anyString())).thenReturn(answer);
        return mockLoader;
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
                return date1;
            }
        };

        Conversation conversation2 = new ConversationImpl(folderPath, null, null, null, null)
        {
            @Override
            public long getIboundModifiedDate()
            {
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
    /**
     * Check detection of scenarios addition
     */
    @Test
    public void testHasDifferencesInConfigurationForDiferentConversationsSize()
    {
        ScenarioLoader scenarioLoader = new ScenarioLoader(scenarioFactory);
        ConversationLoader conversationLoader = new ConversationLoader(conversationFactory, scenarioLoader);

        Conversation conversation1 = null;
        Conversation conversation2 = null;

        try
        {
            conversation1 = conversationLoader.loadSingleConversationInDirectory(folderPath);
            conversation2 = conversationLoader.loadSingleConversationInDirectory(folderPath);
            Scenario conversationScenario = scenarioLoader
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
    /**
     * Check detection of the same number of scenarios with different ids.
     */
    @Test
    public void testHasDifferencesInConfigurationForDiferentConversationScenarios()
    {
        ScenarioLoader scenarioLoader = new ScenarioLoader(scenarioFactory);
        ConversationLoader conversationLoader = new ConversationLoader(conversationFactory, scenarioLoader);

        Conversation conversation1 = null;
        Conversation conversation2 = null;

        try
        {
            conversation1 = new ConversationImpl1(scenarioFactory, conversationLoader.loadSingleConversationInDirectory(
                folderPath));
            conversation2 = new ConversationImpl2(scenarioFactory, conversationLoader.loadSingleConversationInDirectory(
                folderPath));
        }
        catch (IOException e)
        {
            fail(e.getMessage());
        }

        Assert.assertTrue(conversationManager.hasDifferencesInConfiguration(conversation1,
                conversation2));
    }
    
    /**
     * Check detection of the same number of scenarios with different lastModifiedDate.
     */
    @Test
    public void testHasDifferencesInConfigurationForScenariosWithDifferentLastModifiedDate()
    {
        ScenarioLoader scenarioLoader = new ScenarioLoader(scenarioFactory);
        ConversationLoader conversationLoader = new ConversationLoader(conversationFactory, scenarioLoader);

        Conversation conversation1 = null;
        Conversation conversation2 = null;

        try
        {
            conversation1 = conversationLoader.loadSingleConversationInDirectory(folderPath);
            conversation2 = new ConversationImpl3(scenarioFactory, conversationLoader.loadSingleConversationInDirectory(
                folderPath));
        }
        catch (IOException e)
        {
            fail(e.getMessage());
        }

        Assert.assertTrue(conversationManager.hasDifferencesInConfiguration(conversation1,
                conversation2));
    }
    
    /**
     * This class overrides the getScenarios() method of the superior class.
     * @author Oleg Ciobanu (ociobanu@tacitknowledge.com)
     *
     */
    static class ConversationImpl1 extends ConversationImpl{
        Conversation conversation;
        
        public ConversationImpl1(ScenarioFactory scenarioFactory, Conversation conversation)
        {
            super(null, null, null, null, null);
            this.conversation = conversation;
        }
        
        public ConversationImpl1(final String conversationPath, final ScenarioFactory scenarioFactory, final Transport inboundTransport,
                final Transport outboundTransport, final Adapter inboundAdapter,
                final Adapter outboundAdapter)
        {
            super(conversationPath, inboundTransport, outboundTransport, inboundAdapter, outboundAdapter);
        }
        
        public long getIboundModifiedDate()
        {
            return conversation.getIboundModifiedDate(); 
        }
        
        public long getOutboundModifiedDate(){
            return conversation.getOutboundModifiedDate();
        }
        /**
         * This method will return all scenarios without the first one.
         */
        public Map<String, Scenario> getScenarios(){
            Map<String, Scenario> localeScenarios = new HashMap<String, Scenario>();
            Iterator<Entry<String, Scenario>> scenariosIterator = conversation.getScenarios().entrySet().iterator();
            int count = 0;
            while(scenariosIterator.hasNext()){
                if (count==0) {
                    scenariosIterator.next();
                    ++count;
                    continue;
                } else {
                    Entry<String, Scenario> entry = scenariosIterator.next();
                    localeScenarios.put(entry.getKey(), entry.getValue());
                    ++count;
                }
            }
            return localeScenarios;
        }
    }
    /**
     * This class overrides the getScenarios() method of the superior class.
     * @author Oleg Ciobanu ociobanu@tacitknowledge.com
     *
     */
    static class ConversationImpl2 extends ConversationImpl{
        Conversation conversation;
        
        public ConversationImpl2(ScenarioFactory scenarioFactory, Conversation conversation)
        {
            super(null, null, null, null, null);
            this.conversation = conversation;
        }
        
        public ConversationImpl2(final String conversationPath, final ScenarioFactory scenarioFactory, final Transport inboundTransport,
                final Transport outboundTransport, final Adapter inboundAdapter,
                final Adapter outboundAdapter)
        {
            super(conversationPath, inboundTransport, outboundTransport, inboundAdapter, outboundAdapter);
        }
        
        public long getIboundModifiedDate()
        {
            return conversation.getIboundModifiedDate(); 
        }
        
        public long getOutboundModifiedDate(){
            return conversation.getOutboundModifiedDate();
        }
        /**
         * This method will return all scenarios without the last.
         */
        public Map<String, Scenario> getScenarios(){
            Map<String, Scenario> localeScenarios = new HashMap<String, Scenario>();
            Iterator<Entry<String, Scenario>> scenariosIterator = conversation.getScenarios().entrySet().iterator();
            int count = 0;
            while(scenariosIterator.hasNext()){
                if (count==(conversation.getScenarios().size()-1)) {
                    scenariosIterator.next();
                    ++count;
                    continue;
                } else {
                    Entry<String, Scenario> entry = scenariosIterator.next();
                    localeScenarios.put(entry.getKey(), entry.getValue());
                    ++count;
                }
            }
            return localeScenarios;
        }
    }
    /**
     * This class overrides the getScenarios() method of the superior class.
     * @author Oleg Ciobanu ociobanu@tacitknowledge.com
     *
     */
    static class ConversationImpl3 extends ConversationImpl{
        Conversation conversation;
        
        public ConversationImpl3(ScenarioFactory scenarioFactory, Conversation conversation)
        {
            super(null, null, null, null, null);
            this.conversation = conversation;
        }
        
        public ConversationImpl3(final String conversationPath, final ScenarioFactory scenarioFactory, final Transport inboundTransport,
                final Transport outboundTransport, final Adapter inboundAdapter,
                final Adapter outboundAdapter)
        {
            super(conversationPath, inboundTransport, outboundTransport, inboundAdapter, outboundAdapter);
        }
        
        public long getIboundModifiedDate()
        {
            return conversation.getIboundModifiedDate(); 
        }
        
        public long getOutboundModifiedDate(){
            return conversation.getOutboundModifiedDate();
        }
        /**
         * This method will return all scenarios without the last.
         */
        public Map<String, Scenario> getScenarios(){
            Map<String, Scenario> localeScenarios = new HashMap<String, Scenario>();
            Iterator<Entry<String, Scenario>> scenariosIterator = conversation.getScenarios().entrySet().iterator();
            int count = 0;
            while(scenariosIterator.hasNext()){
                if (count==(conversation.getScenarios().size()-1)) {
                    Scenario scenario = scenariosIterator.next().getValue();
                    Scenario newScenario = new ConversationScenarioImplDateModifier(scenario);
                    localeScenarios.put(scenario.getConfigurationFilePath(), newScenario);
                    ++count;
                    continue;
                } else {
                    Entry<String, Scenario> entry = scenariosIterator.next();
                    localeScenarios.put(entry.getKey(), entry.getValue());
                    ++count;
                }
            }
            return localeScenarios;
        }
    }
    /**
     * Adapter class for ConversationScenarioImpl that will override getLatModifiedDate().
     * @author ociobanu
     *
     */
    static class ConversationScenarioImplDateModifier extends ScenarioImpl{
        Scenario scenario;
        public ConversationScenarioImplDateModifier(Scenario scenario){
            super(scenario.getConfigurationFilePath(), scenario.getScriptLanguage(), scenario.getCriteriaScript(), scenario.getTransformationScript());
            this.scenario = scenario;
        }
        /**
         * This method should return a wrong last modified date
         */
        @Override
        public long getLastModifiedDate()
        {
            return scenario.getLastModifiedDate()+100l;
        }
    }
}




