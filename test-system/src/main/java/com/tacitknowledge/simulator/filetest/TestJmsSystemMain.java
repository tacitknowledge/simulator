package com.tacitknowledge.simulator.filetest;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.io.FileUtils;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class sends a jms text message using properties file passed as parameter
 *
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 */
public final class TestJmsSystemMain
{

    /**
     * Connection Factory
     */
    private static ActiveMQConnectionFactory connectionFactory;
    /**
     * Connection
     */
    private static Connection connection;

    /**
     * Session
     */
    private static Session session;
    /**
     * Message Producer
     */
    private static MessageProducer producer;
    /**
     * Destination
     */
    private static Destination destination;
    /**
     * Destination name
     */
    private static String destinationName;

    /**
     * Default Constructor
     */
    private TestJmsSystemMain()
    {

    }

    /**
     * Test for JMS
     *
     * @param args arguments
     * @throws IOException          if an error occurs
     * @throws InterruptedException if an error occurs
     */
    public static void main(final String[] args) throws IOException, InterruptedException
    {
        InputStream stream = null;
        Properties properties = new Properties();
        if (args.length == 0)
        {
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
        properties.load(stream);

        System.out.println(properties.toString());

        String data = FileUtils.readFileToString(new File(properties.getProperty("dataFile")));

        destinationName = properties.getProperty("destinationName");
        String numberOfMessagesStr = properties.getProperty("numberOfMessages");
        Integer numberOfMessages = 1;

        try
        {
            numberOfMessages = Integer.parseInt(numberOfMessagesStr);
        }
        catch (NumberFormatException ex)
        {
            //Swallow exception
        }

        Boolean isDestinationTopic = Boolean.parseBoolean(
                                    properties.getProperty("isDestinationTopic"));
        try
        {
            connectionFactory = new ActiveMQConnectionFactory(properties.getProperty("brokerUrl"));

            connection = getConnection(isDestinationTopic, connectionFactory);
            System.out.println("Connection established succesfully.");

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            System.out.println("Session created.");

            producer = getMessageProducer(isDestinationTopic);
            TextMessage textMessage = session.createTextMessage(data);

            int count = 1;
            while (numberOfMessages > 0)
            {
                producer.send(textMessage);
                System.out.println("Message " + count + " Sent: " + textMessage);
                numberOfMessages--;
                count++;
            }

            producer.close();
            session.close();
            connection.close();

        }
        catch (JMSException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Generates help
     */
    private static void printHelp()
    {
        System.out.println("Properties file name is not specified.");
        System.out.println("Create a file with the following properties:");
        System.out.println("");
        System.out.println("dataFile           -> File that contains the message data.");
        System.out.println("brokerUrl          "
                + "-> The url of the broker system (e.g. tcp://localhost:61616).");
        System.out.println("destinationName    "
                + "-> Name of the queue or topic.");
        System.out.println("isDestinationTopic "
                + "-> The destination is a topic or queue (defaults to false).");
        System.out.println("numberOfMessages   "
                + "-> Number of times to send the message (defaults to 1).");
        System.out.println("");
        System.out.println("Run this command again "
                + "passing the absolute path to the file as parameter.");
    }

    /**
     * Creates a new connection
     * @param isDestinationTopic - Destination or Topic
     * @param factory - ActiveMQConnectionFactory
     * @return New Connection
     * @throws JMSException if an error occurs
     */
    public static Connection getConnection(final Boolean isDestinationTopic,
                                           final ActiveMQConnectionFactory factory) throws
            JMSException
    {
        Connection conn;
        if (isDestinationTopic)
        {
            conn = factory.createTopicConnection();
        }
        else
        {
            conn = factory.createQueueConnection();
        }
        return conn;
    }

    /**
     * Create an message producer
     * @param isDestinationTopic true if it is a topic
     * @return MessageProducer object
     * @throws JMSException if an error occurs
     */
    private static MessageProducer getMessageProducer(final Boolean isDestinationTopic) throws
            JMSException
    {
        if (isDestinationTopic)
        {
            destination = session.createTopic(destinationName);
        }
        else
        {
            destination = session.createQueue(destinationName);
        }
        System.out.println("Destination created. " + destination);

        return session.createProducer(destination);
    }
}
