package com.tacitknowledge.simulator.impl;

import com.tacitknowledge.perf.degradation.proxy.DefaultDegradationStrategy;
import com.tacitknowledge.perf.degradation.proxy.DegradationHandler;
import com.tacitknowledge.perf.degradation.proxy.DegradationStrategy;
import com.tacitknowledge.perf.degradation.proxy.NamedThreadFactory;
import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.Transport;
import org.apache.camel.Exchange;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.*;

/**
 * Factory for creating conversation objects
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public final class ConversationFactory {
    /**
     * Creates a new Conversation from the given transports and adapters.
     *
     * @param conversationPath  path to conversation dir, also used as an id
     * @param inboundTransport  inbound transport
     * @param outboundTransport outbound transport
     * @param inboundAdapter    inbound adapter
     * @param outboundAdapter   outbound adapter
     * @return The created Conversation
     */
    public Conversation createConversation(
            final String conversationPath,
            final Transport inboundTransport,
            final Transport outboundTransport,
            final Adapter inboundAdapter,
            final Adapter outboundAdapter) {
        if (inboundAdapter == null || outboundAdapter == null
                || inboundTransport == null
                || outboundTransport == null) {
            String errorMessage = "Inbound and outbound"
                    + " adapters and transports are required for creating new conversation. ITransport="
                    + inboundTransport + ", OTransport=" + outboundTransport + ", IAdapter=" + inboundAdapter
                    + ", OAdapter=" + outboundAdapter;

            throw new IllegalArgumentException(errorMessage);
        }


        final ConversationImpl conversation = new ConversationImpl(conversationPath, inboundTransport, outboundTransport, inboundAdapter, outboundAdapter);
        return proxy(conversation, conversationPath);
    }

    private Conversation proxy(ConversationImpl conversation, String conversationPath) {
        //todo - mws - temporary short circuit until configs ironed out.  Checking in so that dependency can be tested
        if (true)
            return conversation;


        //insert proxy here. need to check configs
        long serviceDemandTime = 0L;
        long serviceTimeout = 0L;
        double passRate = 1.0;
        int capacity = 10;
        final Boolean accessPolicyFIFO = Boolean.TRUE;
        final int maxQueuedCalls = 10;
        final String factoryName = "conversation[" + conversation.getId() + "]";
        try {
            final Method[] methods = new Method[]{Conversation.class.getMethod("process", Exchange.class)};

            DegradationStrategy degradationStrategy
                    = new DefaultDegradationStrategy(serviceDemandTime,
                    serviceTimeout,
                    passRate,
                    methods

            );
            ThreadFactory threadFactory = new NamedThreadFactory(factoryName);
            ThreadPoolExecutor executorService =  new ThreadPoolExecutor(
                    capacity,
                    capacity,
                    Long.MAX_VALUE,
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(maxQueuedCalls, accessPolicyFIFO),
                    threadFactory,
                    new ThreadPoolExecutor.AbortPolicy()
            );


            final DegradationHandler handler = new DegradationHandler(conversation,
                    executorService, degradationStrategy);

            Object wrappedProxy = Proxy.newProxyInstance(conversation.getClass().getClassLoader(),
                    conversation.getClass().getInterfaces(),
                    handler);

            return (Conversation) wrappedProxy;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Conversation lacks process method", e);
        }

    }


}
