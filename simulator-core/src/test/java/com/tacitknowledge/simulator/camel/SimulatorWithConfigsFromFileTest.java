package com.tacitknowledge.simulator.camel;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.tacitknowledge.simulator.BaseConfigurable;
import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.RouteManager;
import com.tacitknowledge.simulator.configuration.loaders.ConversationsLoader;
import com.tacitknowledge.simulator.transports.FileTransport;

public class SimulatorWithConfigsFromFileTest
{
    @Test
    public void startSimulator() throws Exception
    {
        Resource resource = new ClassPathResource("systems");
        List<Conversation> conversations = ConversationsLoader.loadConversations(resource.getFile()
                .getAbsolutePath());
        
        RouteManager routeManager = new RouteManagerImpl();
        
        for (Conversation conversation : conversations)
        {
            // TODO: refactor this
            // Workaround: relative paths don't work from junit and camel :) Don't know why yet.
            workaroundAbsolutePaths((BaseConfigurable) conversation.getInboundTransport());
            workaroundAbsolutePaths((BaseConfigurable) conversation.getOutboundTransport());
            
            routeManager.activate(conversation);
        }
        
        // Camel executes routes in separate threads. Let's wait a little ...
        Thread.sleep(3000);
        
        File resultFile = new File(getTestsDirectory() + "/output/output.xml");
        assertTrue(resultFile.exists());
    }
    
    private void workaroundAbsolutePaths(BaseConfigurable transport) throws IOException
    {
        if (transport instanceof FileTransport)
        {
            String dir = transport.getParamValue("directoryName");
            transport.setParamValue("directoryName", getTestsDirectory() + "/" + dir);
        }
    }
    
    private String getTestsDirectory() throws IOException
    {
        return new ClassPathResource(".").getFile().getAbsolutePath();
    }
}
