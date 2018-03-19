package nl.oscar.dpi.library.jms.receive;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.Objects;

public class MessageReceiver {

    //URL of the JMS server. DEFAULT_BROKER_URL will just mean that JMS server is on localhost
    private String url;
    private String queueName;

    private Session session;
    private Connection connection;
    private Destination destination;
    private MessageConsumer consumer;

    public MessageReceiver(String queueName, String url) {
        this.queueName = queueName;
        this.url = url;
    }

    public MessageReceiver() {
        this(ActiveMQConnection.DEFAULT_BROKER_URL);
    }

    public MessageReceiver(String queueName) {
        this(queueName, ActiveMQConnection.DEFAULT_BROKER_URL);
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
        if (Objects.nonNull(session)) {
            session.close();
        }
        if (Objects.nonNull(connection)) {
            connection.close();
        }

        consumer = null;
        destination = null;
        session = null;
        connection = null;
    }

    public Message receiveMessage() throws JMSException {
        if (Objects.isNull(connection) || Objects.isNull(session)) {
            start(queueName);
        }

        return consumer.receive();
    }
}