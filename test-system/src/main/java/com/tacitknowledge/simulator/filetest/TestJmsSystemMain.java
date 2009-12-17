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

    public static void main(String[] args) throws IOException, InterruptedException {
        InputStream stream = null;
        Properties properties = new Properties();
        if (args.length == 0) {
            System.out.println("Properties file name is not specified.\n" +
                    "Using default configuration");
            stream = TestJmsSystemMain.class.getClassLoader().getResourceAsStream("testjmssystem.properties");
        } else {
            stream = new FileInputStream(args[0]);
        }
        properties.load(stream);

        String data = FileUtils.readFileToString(new File(properties.getProperty("dataFile")));
        System.out.println(properties.toString());
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(properties.getProperty("brokerUrl"));
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(session.createQueue(properties.getProperty("queueName")));
            TextMessage textMessage = session.createTextMessage(data);
            producer.send(textMessage);

            producer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
