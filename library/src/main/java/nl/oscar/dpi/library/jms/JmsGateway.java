package nl.oscar.dpi.library.jms;

import nl.oscar.dpi.library.jms.receive.ApacheMqMessageReceiver;
import nl.oscar.dpi.library.jms.receive.MessageReceiver;
import nl.oscar.dpi.library.jms.receive.impl.ObjectMessageListener;
import nl.oscar.dpi.library.jms.send.MessageSender;

import javax.jms.JMSException;
import java.io.Serializable;

public abstract class JmsGateway<S extends Serializable, R> {

    private MessageSender sender;
    private ApacheMqMessageReceiver receiver;

    public JmsGateway(String sendQueueName, String receiveQueueName) {
        try {
            sender = new MessageSender(sendQueueName);
            receiver = new ApacheMqMessageReceiver(new MessageReceiver(receiveQueueName));
            sender.start();
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    public void send(S object) {
        try {
            sender.sendObjectMessage(object);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void send(S object, String corr_id) {
        try {
            sender.sendObjectMessage(object, corr_id);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void setListener(ObjectMessageListener<R> listener) {
        receiver.setListener(listener);
        receiver.start();
    }

}
