package com.tacitknowledge.simulator.camel;

import com.tacitknowledge.simulator.Conversation;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import java.io.IOException;

/**
 * @author nikitabelenkiy
 */
public class LoggingBean
{

    private Logger logger;

    private boolean input;

    static
    {
        Logger logger = Logger.getLogger("com.tacitknowledge.conversations");
        logger.removeAllAppenders();
    }

    public LoggingBean(boolean input, Conversation conversation) throws IOException
    {
        this.input = input;
        logger = Logger.getLogger("com.tacitknowledge.conversations." + conversation.getId());

        if (input)
        {
            configureLogger(conversation);
        }
    }

    private void configureLogger(Conversation conversation)
        throws IOException
    {
        //remove parent appenders
        logger.setLevel(org.apache.log4j.Level.ALL);

        //create a new appender for each conversation
        RollingFileAppender newAppender = new RollingFileAppender(new PatternLayout("%d{MMM dd HH:mm:ss} [%t] %-5p %c %x - %m%n"),
            "Conversation " + conversation.getId() + ".log", true);
        newAppender.setName("Conversation " + conversation.getId());
        newAppender.setBufferSize(1024 * 3);
        newAppender.setMaxBackupIndex(5);
        newAppender.setThreshold(Level.ALL);
        logger.addAppender(newAppender);
    }

    /**
     * logs data and returns input as result
     *
     * @param body
     * @return
     */
    public String process(String body)
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
        logger.info(body);
        if (!input)
        {
            //juts blank lines
            logger.info("");
            logger.info("");
            logger.info("");
            logger.info("");
            logger.info("");
        }
        return body;
    }
}
