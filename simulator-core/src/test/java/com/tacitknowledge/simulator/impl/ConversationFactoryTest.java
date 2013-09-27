package com.tacitknowledge.simulator.impl;

import com.tacitknowledge.perf.degradation.proxy.DegradationHandler;
import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.BaseConfigurable;
import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.Transport;
import com.tacitknowledge.simulator.formats.PlainTextAdapter;
import com.tacitknowledge.simulator.transports.HttpTransport;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by IntelliJ IDEA.
 * User: mshort
 * Date: 7/29/13
 * Time: 11:25 AM
 */
public class ConversationFactoryTest {

    @Test
    public void testProxyDefaults() {
        final HashMap<String, String> parameters = new HashMap<String, String>();
        Transport inboundTransport = new HttpTransport(new BaseConfigurable(parameters));
        Transport outboundTransport = new HttpTransport(new BaseConfigurable(parameters));
        Adapter inboundAdapter = new PlainTextAdapter();
        Adapter outboundAdapter = new PlainTextAdapter();

        ConversationFactory factory = new ConversationFactory();
        Conversation conversation =
                factory.createConversation("somepath",inboundTransport,outboundTransport,inboundAdapter,outboundAdapter);
        Assert.assertTrue("this should be a proxy", conversation instanceof Proxy);
        Proxy proxy = (Proxy) conversation;
        DegradationHandler handler = (DegradationHandler) Proxy.getInvocationHandler(proxy);
        Assert.assertEquals("default pool",5,handler.getExecutorService().getCorePoolSize());
        Assert.assertTrue("should be linked", handler.getExecutorService().getQueue() instanceof LinkedBlockingQueue );
        Assert.assertEquals("passRate",new Double(1.0),handler.getDegradationStrategy().getPassRate());
        Assert.assertEquals("demandTime",new Long(0),handler.getDegradationStrategy().getServiceDemandTime());
        Assert.assertEquals("serviceTimeout",new Long(0),handler.getDegradationStrategy().getServiceTimeout());
    }

    @Test
    public void testProxyOverrides() {
        final HashMap<String, String> parameters = new HashMap<String, String>();
        Transport inboundTransport = new HttpTransport(new BaseConfigurable(parameters));
        Transport outboundTransport = new HttpTransport(new BaseConfigurable(parameters));
        Adapter inboundAdapter = new PlainTextAdapter();
        Adapter outboundAdapter = new PlainTextAdapter();

        parameters.put(ConversationFactory.PASS_RATE,"0.5");
        parameters.put(ConversationFactory.DEMAND_TIME,"2000");
        parameters.put(ConversationFactory.SERVICE_TIMEOUT,"2500");
        parameters.put(ConversationFactory.POOL_CAPACITY,"2");
        parameters.put(ConversationFactory.MAX_QUEUE,"3");

        ConversationFactory factory = new ConversationFactory();
        Conversation conversation =
                factory.createConversation("somepath",inboundTransport,outboundTransport,inboundAdapter,outboundAdapter);
        Assert.assertTrue("this should be a proxy", conversation instanceof Proxy);
        Proxy proxy = (Proxy) conversation;
        DegradationHandler handler = (DegradationHandler) Proxy.getInvocationHandler(proxy);
        Assert.assertEquals("default pool",2,handler.getExecutorService().getCorePoolSize());
        Assert.assertTrue("should be linked", handler.getExecutorService().getQueue() instanceof ArrayBlockingQueue);
        Assert.assertEquals("passRate",new Double(0.5),handler.getDegradationStrategy().getPassRate());
        Assert.assertEquals("demandTime",new Long(2000),handler.getDegradationStrategy().getServiceDemandTime());
        Assert.assertEquals("serviceTimeout",new Long(2500),handler.getDegradationStrategy().getServiceTimeout());
    }

}
