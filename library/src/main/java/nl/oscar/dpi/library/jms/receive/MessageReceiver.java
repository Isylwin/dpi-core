package nl.oscar.dpi.library.jms.receive;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.Objects;

public class MessageReceiver {

    //URL of the JMS server. DEFAULT_BROKER_URL will just mean that JMS server is on localhost
    private String url;

    private Session session;
    private Connection connection;
    private Destination destination;
    private MessageConsumer consumer;

    public MessageReceiver(String url) {
        this.url = url;
    }

    public MessageReceiver() {
        this(ActiveMQConnection.DEFAULT_BROKER_URL);
    }

    public void start(String queueName) throws JMSException {
        // Getting JMS connection from the server and starting it
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        connection = connectionFactory.createConnection();
        connection.start();

        //Creating a non transactional session to send/receive JMS message.
        session = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);

        destination = session.createQueue(queueName);

        consumer = session.createConsumer(destination);
    }

    public void stop() throws JMSException {
        session.close();
        connection.close();

        consumer = null;
        destination = null;
        session = null;
        connection = null;
    }

    public Message receiveMessage(String queueName) throws JMSException {
        if (Objects.isNull(connection) || Objects.isNull(session)) {
            start(queueName);
        }

        return consumer.receive();
    }
}