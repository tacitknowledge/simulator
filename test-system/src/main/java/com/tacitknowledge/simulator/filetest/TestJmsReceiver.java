package com.tacitknowledge.simulator.filetest;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * This class listens for messages on a queue or topic on a jms server
 * given by the file that is passed as a parameter (or by the default file).
 *
 * @author Daniel Valencia (mailto:dvalencia@tacitknowledge.com)
 */

public final class TestJmsReceiver
{
    /**
     * Message listener attribute
     */
    private static MessageListener messageListener;

    /**
     * Default constructor
     */
    private TestJmsReceiver()
    {
    }

    /**
     * Tests jms as receiver
     *
     * @param args - arguments
     * @throws IOException if an error occurs
     */
    public static void main(final String[] args) throws IOException
    {
        InputStream stream = null;
        Properties properties = new Properties();
        if (args.length == 0)
        {
            System.out.println("Properties file name is not specified.");
            printHelp();
            System.exit(0);
        }
        else
        {
            if (args[0].equals("--help"))
            {
                printHelp();
                System.exit(0);
            }
            else
            {
                stream = new FileInputStream(args[0]);
            }
        }

        if (messageListener == null)
        {
            messageListener = new MessageListener()
            {
                public void onMessage(final Message message)
                {
                    if (message instanceof ActiveMQTextMessage)
                    {
                        try
                        {
                            System.out.println("Message Received: "
                                    + ((ActiveMQTextMessage) message).getText());
                        }
                        catch (JMSException e)
                        {
                            System.out.println("Error getting the message text.");
                        }
                    }
                }
            };
        }


        properties.load(stream);

        System.out.println(properties.toString());

        String destinationName = properties.getProperty("destinationName");
        Boolean isDestinationTopic = Boolean.parseBoolean(
                properties.getProperty("isDestinationTopic"));

        ActiveMQConnectionFactory connectionFactory =
                new ActiveMQConnectionFactory(properties.getProperty("brokerUrl"));
        Connection connection = null;

        try
        {
            connection = TestJmsSystemMain.getConnection(isDestinationTopic, connectionFactory);
            System.out.println("Connection created.");

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            System.out.println("Session created. " + session);

            MessageConsumer consumer =
                    createMessageConsumer(session, isDestinationTopic, destinationName);

            consumer.setMessageListener(messageListener);
            System.out.println("Message Listener created: " + messageListener);


            connection.start();
            char answer = '\0';

            System.out.println("To end program, enter Q or q, "
                    + "then <return>");
            InputStreamReader inputStreamReader = new InputStreamReader(System.in);
            while (!((answer == 'q') || (answer == 'Q')))
            {
                try
                {
                    answer = (char) inputStreamReader.read();
                }
                catch (IOException e)
                {
                    System.out.println("I/O exception: "
                            + e.toString());
                }
            }

        }
        catch (JMSException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (connection != null)
            {
                try
                {
                    connection.close();
                }
                catch (JMSException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Display help
     */
    private static void printHelp()
    {
        System.out.println("Create a file with the following properties:");
        System.out.println("");
        System.out.println("brokerUrl          "
                + "-> The url of the broker system (e.g. tcp://localhost:61616).");
        System.out.println("destinationName    -> Name of the queue or topic.");
        System.out.println("isDestinationTopic -> "
                + "The destination is a topic or queue (defaults to false).");
        System.out.println("");
        System.out.println("Run this command again passing "
                + "the absolute path to the file as parameter.");
    }

    /**
     * Create a message consumer
     *
     * @param session            - JMS session
     * @param isDestinationTopic - true if it is listening to topics
     * @param destinationName    - queu or topic name
     * @return Message Consumer
     * @throws JMSException if something goes wrong
     */
    private static MessageConsumer createMessageConsumer(final Session session,
                                                         final Boolean isDestinationTopic,
                                                         final String destinationName) throws
                                                         JMSException
    {
        Destination destination;
        if (isDestinationTopic)
        {
            destination = session.createTopic(destinationName);
        }
        else
        {
            destination = session.createQueue(destinationName);
        }

        System.out.println("Destination created. " + destination);

        return session.createConsumer(destination);
    }


}
