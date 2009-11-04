package com.tacitknowledge.simulator.camel;

import com.tacitknowledge.simulator.Adapter;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

/**
 * @author nikitabelenkiy
 */
public class AdapterWrapper implements Processor
{
    private Adapter adapter;


    public static final Logger logger = Logger.getLogger(AdapterWrapper.class);

    public AdapterWrapper(Adapter adapter)
    {

        this.adapter = adapter;
    }

    public void process(Exchange exchange) throws Exception
    {
        logger.info("running adapter " + adapter);
    }
}
