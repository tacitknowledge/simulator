package com.tacitknowledge.simulator.filetest;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.*;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * This class listens for messages on a queue or topic on a jms server
 * given by the file that is passed as a parameter (or by the default file).
 *
 * @author Daniel Valencia (mailto:dvalencia@tacitknowledge.com)
 */

public class TestJmsReceiver {
    private static MessageListener messageListener;

    public TestJmsReceiver(MessageListener listener){
        messageListener = listener;
    }

    public static void main(String[] args) throws IOException {
        InputStream stream = null;
        Properties properties = new Properties();
        if (args.length == 0) {
            System.out.println("Properties file name is not specified.");
            System.exit(0);
        } else {
            stream = new FileInputStream(args[0]);
        }

        if(messageListener == null){
            messageListener = new MessageListener(){
                public void onMessage(Message message) {
                    if(message instanceof ActiveMQTextMessage){
                        try {
                            System.out.println("Message Received: " + ((ActiveMQTextMessage)message).getText());
                        } catch (JMSException e) {
                            System.out.println("Error getting the message text.");
                        }
                    }
                }
            };
        }


        properties.load(stream);

        System.out.println(properties.toString());

        String destinationName = properties.getProperty("destinationName");
        Boolean isDestinationTopic = Boolean.parseBoolean(properties.getProperty("isDestinationTopic"));

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(properties.getProperty("brokerUrl"));
        Connection connection = null;

        try {
            connection = TestJmsSystemMain.getConnection(isDestinationTopic, connectionFactory);
            System.out.println("Connection created.");

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            System.out.println("Session created. " + session);

            MessageConsumer consumer = createMessageConsumer(session, isDestinationTopic, destinationName);

            consumer.setMessageListener(messageListener);
            System.out.println("Message Listener created: " + messageListener);


            connection.start();
            char answer = '\0';

            System.out.println("To end program, enter Q or q, " +
                    "then <return>");
            InputStreamReader inputStreamReader = new InputStreamReader(System.in);
            while (!((answer == 'q') || (answer == 'Q'))) {
                try {
                    answer = (char) inputStreamReader.read();
                } catch (IOException e) {
                    System.out.println("I/O exception: "
                            + e.toString());
                }
            }
            
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            if(connection != null){
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static MessageConsumer createMessageConsumer(Session session, Boolean isDestinationTopic, String destinationName) throws JMSException {
        Destination destination;
        if(isDestinationTopic){
            destination = session.createTopic(destinationName);
        }else{
            destination = session.createQueue(destinationName);
        }

        System.out.println("Destination created. " + destination);

        return session.createConsumer(destination);
    }


}
