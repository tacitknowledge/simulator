package com.tacitknowledge.simulator.camel;

import com.tacitknowledge.simulator.Adapter;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

/**
 * Wrapper for the Adapter implementations.
 * Overcomes the need for the adapter to be camel aware. 
 *
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 * @author Alexandru Dereveanco (adereveanco@tacitknowledge.com)
 */
public class AdapterWrapper implements Processor
{
    /** Adapter property */
    private Adapter adapter;

    /**
     * Logger for the AdapterWrapper class.
     */
    public static final Logger logger= Logger.getLogger(AdapterWrapper.class);

    /**
     * Constructor for the AdapterWrapper class
     * @param adapter the adapter to be wrapped.
     */
    public AdapterWrapper(Adapter adapter)
    {
        this.adapter = adapter;
    }

    /**
     * Processes the exchange object received in the camel route.
     * @param exchange object of data used in camel routes.
     */
    public void process(Exchange exchange) throws Exception
    {
        logger.info("running adapter " + adapter);
    }
}
