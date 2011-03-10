package com.tacitknowledge.simulator.configuration.loaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.Configurable;
import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.ConversationScenario;
import com.tacitknowledge.simulator.ScenarioParsingException;
import com.tacitknowledge.simulator.Transport;
import com.tacitknowledge.simulator.formats.AdapterFactory;
import com.tacitknowledge.simulator.impl.ConversationImpl;
import com.tacitknowledge.simulator.transports.TransportFactory;

public class ConversationLoader
{
    private static final String SCENARIO_FILE_EXTENSION = ".scn";

    private static final String INBOUND_CONFIG = "inbound.properties";

    private static final String OUTBOUND_CONFIG = "outbound.properties";

    private static final String TYPE = "type";

    private static final String FORMAT = "format";

    /**
     * Logger for the EventDispatcherImpl class.
     */
    private static Logger logger = LoggerFactory.getLogger(ConversationLoader.class);

    private ConversationLoader()
    {}

    /**
     * Parse and constructs conversation from a given directory
     * 
     * @param conversationDir conversation directory (usually /systems/asystem/aconversation)
     * @return Conversation
     * @throws IOException
     */
    public static Conversation parseConversationFromPath(String conversationDir) throws IOException
    {
        Transport inTransport = getTransport(Configurable.BOUND_IN, conversationDir);
        Transport outTransport = getTransport(Configurable.BOUND_OUT, conversationDir);

        Adapter<Object> inAdapter = getAdapter(Configurable.BOUND_IN, conversationDir);
        Adapter<Object> outAdapter = getAdapter(Configurable.BOUND_OUT, conversationDir);

        if (inTransport == null || outTransport == null || inAdapter == null || outAdapter == null)
        {
            logger.warn("Could not parse conversation inbound/outbound config. Conversation :"
                    + conversationDir);
            return null;
        }

        ConversationImpl conversation = new ConversationImpl(conversationDir, inTransport,
                outTransport, inAdapter, outAdapter);

        loadConversationScenarios(conversation, conversationDir);

        return conversation;
    }

    /**
     * Loads all conversation scenarios
     * 
     * @param conversation conversation
     * @param conversationDir conversation directory
     * @throws IOException
     */
    private static void loadConversationScenarios(Conversation conversation, String conversationDir)
            throws IOException
    {
        Resource resource = new FileSystemResource(conversationDir);

        File[] scenarioFiles = resource.getFile().listFiles(new FilenameFilter()
        {

            public boolean accept(File dir, String name)
            {
                return name.endsWith(SCENARIO_FILE_EXTENSION);
            }
        });

        // TODO: ID will be removed soon (artifact from MySQL)
        int scenarioId = 1;

        for (File scenarioFile : scenarioFiles)
        {
            try
            {
                ConversationScenario scenario = ScenarioLoader.parseScenarioFromFile(scenarioFile
                        .getAbsolutePath());

                conversation.addOrUpdateScenario(scenarioId, scenario.getScriptLanguage(),
                        scenario.getCriteriaScript(), scenario.getTransformationScript());
            }
            catch (ScenarioParsingException ex)
            {
                logger.error(ex.getMessage(), ex);
            }

            scenarioId++;
        }
    }

    private static Transport getTransport(int bound, String conversationDir) throws IOException
    {
        String configFileName = bound == Configurable.BOUND_IN ? INBOUND_CONFIG : OUTBOUND_CONFIG;

        String configFilePath = new File(conversationDir, configFileName).getAbsolutePath();

        InputStream is = new FileInputStream(configFilePath);

        try
        {
            Properties properties = loadConversationProperties(is);

            return getConversationTransport(Configurable.BOUND_IN, properties);
        }
        finally
        {
            is.close();
        }
    }

    private static Adapter<Object> getAdapter(int bound, String conversationDir) throws IOException
    {
        String configFileName = bound == Configurable.BOUND_IN ? INBOUND_CONFIG : OUTBOUND_CONFIG;

        String configFilePath = new File(conversationDir, configFileName).getAbsolutePath();

        InputStream is = new FileInputStream(configFilePath);

        try
        {
            Properties properties = loadConversationProperties(is);

            return getConversationAdapter(Configurable.BOUND_IN, properties);
        }
        finally
        {
            is.close();
        }
    }

    private static Properties loadConversationProperties(InputStream inputStream)
            throws IOException
    {
        Properties properties = new Properties();
        properties.load(inputStream);

        return properties;
    }

    private static Transport getConversationTransport(int bound, Properties properties)
            throws IOException
    {
        String type = properties.getProperty(TYPE);

        if (type == null)
        {
            logger.warn("Could not find mandatory property 'type'.");
            return null;
        }

        return TransportFactory.createTransport(bound, type.toUpperCase(), properties);
    }

    private static Adapter<Object> getConversationAdapter(int bound, Properties properties)
            throws IOException
    {
        String format = properties.getProperty(FORMAT);

        if (format == null)
        {
            logger.warn("Could not find mandatory property 'format'.");
            return null;
        }

        return AdapterFactory.createAdapter(bound, format.toUpperCase(), properties);
    }
}