package com.tacitknowledge.simulator.camel;

import com.tacitknowledge.simulator.Conversation;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.apache.camel.Exchange;

import java.io.IOException;

/**
 * @author nikitabelenkiy
 */
public class LoggingBean
{
    /**
     * Logger attribute
     */
    private Logger logger;

    /**
     * Flag to determine if the message is for unpit or output
     */
    private boolean input;

    static
    {
        Logger logger = LoggerFactory.getLogger("com.tacitknowledge.conversations");
        //logger.removeAllAppenders();
    }

    /**
     * Default Constructor
     * @param input - true if message is for input, false if it is for output
     * @param conversation - Conversation object
     * @throws IOException - If not able to create new log file
     */
    public LoggingBean(final boolean input, final Conversation conversation) throws IOException
    {
        this.input = input;
        logger = LoggerFactory.getLogger("com.tacitknowledge.conversations." + conversation.getId());

        if (input)
        {
            configureLogger(conversation);
        }
    }

    /**
     * Create an new log file for a conversation
     * @param conversation - Conversation object
     * @throws IOException -  If a log file cannot be created
     */
    private void configureLogger(final Conversation conversation)
        throws IOException
    {
        //remove parent appenders
        //logger.setLevel(org.apache.log4j.Level.ALL);

        //create a new appender for each conversation
        RollingFileAppender newAppender = new RollingFileAppender(
                new PatternLayout("%d{MMM dd HH:mm:ss} [%t] %-5p %c %x - %m%n"),
            "Conversation " + conversation.getId() + ".log", true);
        newAppender.setName("Conversation " + conversation.getId());
        newAppender.setBufferSize(1024 * 3);
        newAppender.setMaxBackupIndex(5);
        newAppender.setThreshold(Level.ALL);
        //logger.addAppender(newAppender);
    }

    /**
     * logs data and returns input as result
     *
     * @param exchange - Exchange object
     */
    public void process(final Exchange exchange)
    {
        logger.info("-----------------------------------");
        if (input)
        {
            logger.info("INCOMING_MESSAGE");
        }
        else
        {
            logger.info("RESPONSE_MESSAGE");
        }
        logger.info(exchange.toString());
        if (!input)
        {
            //juts blank lines
            logger.info("");
            logger.info("");
            logger.info("");
            logger.info("");
            logger.info("");
        }
    }
}
