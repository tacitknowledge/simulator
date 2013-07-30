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
import java.util.Properties;
import java.util.concurrent.*;

/**
 * Factory for creating conversation objects
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public final class ConversationFactory {

    /**
     * Base demandTime to configure proxy degradation.  Use long millis
     */
    public static final String DEMAND_TIME = "demandTime";

    /**
     * demand time will degrade up to approximately the service timeout with concurrency
     * Use long millis
     */
    public static final String SERVICE_TIMEOUT = "serviceTimeout";
    /**
     * This should probably be left to default (1.0).  takes a double value between 0 and 1
     */
    public static final String PASS_RATE = "passRate";
    /**
     * Sets the core and max size on underlying ThreadPoolExecutor for this conversation. Use int
     */
    public static final String POOL_CAPACITY = "poolCapacity";
    /**
     * Integer value specifying the blocking queue bounding size.  RejectionExecution is AbortPolicy.
     * If unset, defaults to -1
     * If less than zero, the blocking queue is unbounded
     */
    public static final String MAX_QUEUE = "maxQueue";

    /**
     * Creates a new Conversation from the given transports and adapters.  This will wrap the Conversation
     * in a Proxy, which may degrade the process(exchange) method if configured.
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

        try {

            final String factoryName = "conversation[" + conversation.getId() + "]";
            final Method[] methods = new Method[]{Conversation.class.getMethod("process", Exchange.class)};

            Properties properties = new Properties();
            properties.putAll(conversation.getInboundTransport().getConfigurable().getParameters());

            //insert proxy here. need to check configs
            long serviceDemandTime = Long.parseLong(properties.getProperty(DEMAND_TIME, "0"));
            long serviceTimeout = Long.parseLong(properties.getProperty(SERVICE_TIMEOUT, "0"));
            double passRate = Double.parseDouble(properties.getProperty(PASS_RATE, "1.0"));
            int poolCapacity = Integer.parseInt(properties.getProperty(POOL_CAPACITY, "5"));
            //set to -1 if not specified.  This will trigger an unbounded queue
            int maxQueue = Integer.parseInt(properties.getProperty(MAX_QUEUE, "-1"));

            BlockingQueue<Runnable> poolBlockingQueue = createBlockingQueue(maxQueue);

            DegradationStrategy degradationStrategy
                    = new DefaultDegradationStrategy(serviceDemandTime,
                    serviceTimeout,
                    passRate,
                    methods
            );
            ThreadFactory threadFactory = new NamedThreadFactory(factoryName);

            ThreadPoolExecutor executorService = new ThreadPoolExecutor(
                    poolCapacity,
                    poolCapacity,
                    Long.MAX_VALUE,
                    TimeUnit.SECONDS,
                    poolBlockingQueue,
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

    /**
     *
     * @param maxQueue
     * @return unbounded LinkedBlockingQueue if maxQueue is < 0.  ArrayBlockingQueue with capacity specified otherwise
     */
    private BlockingQueue<Runnable> createBlockingQueue(int maxQueue) {
        BlockingQueue<Runnable> poolBlockingQueue
                = new LinkedBlockingQueue<Runnable>();

        if (maxQueue >= 0) {
            final Boolean accessPolicyFIFO = Boolean.TRUE;
            poolBlockingQueue
                    = new ArrayBlockingQueue<Runnable>(maxQueue, accessPolicyFIFO);
        }
        return poolBlockingQueue;
    }


}
