package nl.oscar.dpi.library.jms.receive;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApacheMqMessageReceiver {

    private Collection<MessageListener> listeners;
    private ExecutorService executors;
    private MessageReceiver receiver;

    private boolean isShutdown;

    public ApacheMqMessageReceiver(MessageReceiver receiver) {
        this.listeners = new ArrayList<>();
        this.executors = Executors.newSingleThreadExecutor();
        this.receiver = receiver;
    }

    public void addListeners(MessageListener listeners) {
        this.listeners.add(listeners);
    }

    public void start() {
        if (Objects.nonNull(listeners)) {
            isShutdown = false;

            executors.execute(() -> {
                while (!isShutdown) {
                    try {
                        Message message = receiver.receiveMessage();
                        System.out.println("Received message " + message.toString());
                        listeners.forEach(e -> e.receiveMessage(message));
                    } catch (JMSException e) {
                        System.out.println(e.getMessage());
                    }
                }
            });
        }
    }

    public void stop() {
        isShutdown = true;
        try {
            receiver.stop();
        } catch (JMSException e) {
            System.out.println(e.getMessage());
        }
        executors.shutdown();
    }
}
