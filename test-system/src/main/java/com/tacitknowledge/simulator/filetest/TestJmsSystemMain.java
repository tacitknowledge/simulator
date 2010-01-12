package com.tacitknowledge.simulator.filetest;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.io.FileUtils;

import javax.jms.*;
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
public class TestJmsSystemMain {

    static ActiveMQConnectionFactory connectionFactory;
    static Connection connection;
    static Session session;
    static MessageProducer producer;
    static Destination destination;
    static String destinationName;

    public static void main(String[] args) throws IOException, InterruptedException {
        InputStream stream = null;
        Properties properties = new Properties();
        if (args.length == 0) {
            printHelp();
            System.exit(0);
        } else {
            if(args[0].equals("--help")){
                printHelp();
                System.exit(0);
            }else{                
                stream = new FileInputStream(args[0]);
            }
        }
        properties.load(stream);

        System.out.println(properties.toString());

        String data = FileUtils.readFileToString(new File(properties.getProperty("dataFile")));

        destinationName = properties.getProperty("destinationName");
        String numberOfMessagesStr = properties.getProperty("numberOfMessages");
        Integer numberOfMessages = 1;

        try{
            numberOfMessages = Integer.parseInt(numberOfMessagesStr);
        }catch(NumberFormatException ex){}

        Boolean isDestinationTopic = Boolean.parseBoolean(properties.getProperty("isDestinationTopic"));
        try {
            connectionFactory = new ActiveMQConnectionFactory(properties.getProperty("brokerUrl"));

            connection = getConnection(isDestinationTopic, connectionFactory);
            System.out.println("Connection established succesfully.");

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            System.out.println("Session created.");

            producer = getMessageProducer(isDestinationTopic);
            TextMessage textMessage = session.createTextMessage(data);

            while(numberOfMessages > 0){
                producer.send(textMessage);
                System.out.println("Message " + numberOfMessages + " Sent: " + textMessage);
                numberOfMessages--;
            }

            producer.close();            
            session.close();
            connection.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private static void printHelp(){
        System.out.println("Properties file name is not specified.");
        System.out.println("Create a file with the following properties:");
        System.out.println("dataFile           -> File that contains the message data.");
        System.out.println("brokerUrl          -> The url of the broker system (e.g. tcp://localhost:61616).");
        System.out.println("destinationName    -> Name of the queue or topic.");
        System.out.println("isDestinationTopic -> The destination is a topic or queue (defaults to false).");
        System.out.println("numberOfMessages   -> Number of times to send the message (defaults to 1).");
        System.out.println("");
        System.out.println("Run this command again passing the absolute path to the file as parameter.");
    }

    public static Connection getConnection(Boolean isDestinationTopic, ActiveMQConnectionFactory factory) throws JMSException {
        Connection conn;
        if(isDestinationTopic){
            conn = factory.createTopicConnection();
        } else {
            conn = factory.createQueueConnection();
        }
        return conn;
    }

    private static MessageProducer getMessageProducer(Boolean isDestinationTopic) throws JMSException {
        if(isDestinationTopic){
            destination = session.createTopic(destinationName);
        }else{
            destination = session.createQueue(destinationName);
        }
        System.out.println("Destination created. " + destination);

        return session.createProducer(destination);
    }
}
