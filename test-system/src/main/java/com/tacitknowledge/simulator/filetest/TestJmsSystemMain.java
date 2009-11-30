package com.tacitknowledge.simulator.filetest;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Date: 27.11.2009
 * Time: 11:32:46
 *
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 */
public class TestJmsSystemMain {


    private static final String DATA="<employees>\n" +
            "    <employee>\n" +
            "        <name>John</name>\n" +
            "        <title>Manager</title>\n" +
            "    </employee>\n" +
            "    <employee>\n" +
            "        <name>Sara</name>\n" +
            "        <title>Clerk</title>\n" +
            "    </employee>\n" +
            "    <reportDate>2009-11-05</reportDate>\n" +
            "    <roles>\n" +
            "        <role>Clerk</role>\n" +
            "        <role>Manager</role>\n" +
            "        <role>Accountant</role>\n" +
            "    </roles>\n" +
            "</employees>";


    public static void main(String[] args) {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(session.createQueue("myqueue"));
            TextMessage textMessage = session.createTextMessage(DATA);
            producer.send(textMessage);

            producer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
