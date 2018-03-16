package nl.oscar.dpi.library.jms.receive.impl;

import nl.oscar.dpi.library.jms.receive.MessageListener;

import javax.jms.Message;
import javax.jms.ObjectMessage;

public abstract class ObjectMessageListener<T> implements MessageListener {

    @Override
    public void receiveMessage(Message message) {
        try {
            if (message instanceof ObjectMessage) {
                ObjectMessage objectMessage = (ObjectMessage) message;
                T object = (T) objectMessage.getObject();
                receivedObjectMessage(object, objectMessage);
            }
        } catch (Exception e) {
            System.out.println("Error parsing message");
        }
    }

    protected abstract void receivedObjectMessage(T object, ObjectMessage message);
}
