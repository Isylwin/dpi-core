package nl.oscar.dpi.library.jms.receive;

import javax.jms.Message;

public interface MessageListener {

    void receiveMessage(Message message);
}
