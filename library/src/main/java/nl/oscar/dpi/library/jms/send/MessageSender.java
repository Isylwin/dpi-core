package nl.oscar.dpi.library.jms.send;

import com.sun.istack.internal.NotNull;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.Serializable;
import java.util.Objects;

public class MessageSender {

    //URL of the JMS server. DEFAULT_BROKER_URL will just mean that JMS server is on localhost
    private String url;
    private String queueName;

    private Session session;
    private Connection connection;
    private Destination destination;
    private MessageProducer producer;

    public MessageSender(String queueName, String url) {
        this.url = url;
        this.queueName = queueName;
    }

    public MessageSender(String queueName) {
        this(queueName, ActiveMQConnection.DEFAULT_BROKER_URL);
    }

    public void start() throws JMSException {
        // Getting JMS connection from the server and starting it
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        connection = connectionFactory.createConnection();
        connection.start();

        //Creating a non transactional session to send/receive JMS message.
        session = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);

        destination = session.createQueue(queueName);

        // MessageProducer is used for sending messages to the queue.
        producer = session.createProducer(destination);
    }

    public void stop() throws JMSException {
        connection.close();
        session.close();

        connection = null;
        session = null;
    }

    public <T extends Serializable> void sendObjectMessage(T object) throws JMSException {
        sendObjectMessage(object, destination, null, null);
    }

    public <T extends Serializable> void sendObjectMessage(T object, int aggId) throws JMSException {
        sendObjectMessage(object, destination, null, aggId);
    }

    public <T extends Serializable> void sendObjectMessage(T object, String corrId) throws JMSException {
        sendObjectMessage(object, destination, corrId, null);
    }

    public <T extends Serializable> void sendObjectMessage(T object, Destination destination) throws JMSException {
        sendObjectMessage(object, destination, null, null);
    }

    public <T extends Serializable> void sendObjectMessage(@NotNull T object, @NotNull Destination destination, String corrId, Integer aggId) throws JMSException {
        if (Objects.isNull(connection) || Objects.isNull(session)) {
            start();
            destination = this.destination;
        }

        ObjectMessage message = session.createObjectMessage(object);

        message.setJMSCorrelationID(corrId);

        producer.send(destination, message);

        System.out.println("Sending message@@ '" + message.getObject().toString() + "'");
    }


    public void sendTextMessage(String textMessage, String queueName) throws JMSException {
        if (Objects.isNull(connection) || Objects.isNull(session)) {
            start();
        }

        connection.start();
        //Destination represents here our queue 'JCG_QUEUE' on the JMS server.
        //The queue will be created automatically on the server.
        Destination destination = session.createQueue(queueName);

        // MessageProducer is used for sending messages to the queue.
        MessageProducer producer = session.createProducer(destination);

        // We will send a small text message saying 'Hello World!!!'
        TextMessage message = session
                .createTextMessage(textMessage);

        // Here we are sending our message!
        producer.send(message);

        System.out.println("Sending message@@ '" + message.getText() + "'");
    }
}
