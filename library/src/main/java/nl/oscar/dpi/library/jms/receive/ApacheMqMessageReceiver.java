package nl.oscar.dpi.library.jms.receive;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApacheMqMessageReceiver {

    private MessageListener listener;
    private ExecutorService executors;
    private MessageReceiver receiver;

    private boolean isShutdown;

    public ApacheMqMessageReceiver(MessageReceiver receiver) {
        this.executors = Executors.newSingleThreadExecutor();
        this.receiver = receiver;
    }

    public void setListener(MessageListener listener) {
        this.listener = listener;
    }

    public void start(String queueName) {
        if (Objects.nonNull(listener)) {
            isShutdown = false;

            executors.execute(() -> {
                while (!isShutdown) {
                    try {
                        Message message = receiver.receiveMessage(queueName);
                        System.out.println("Received message " + message.toString());
                        listener.receiveMessage(message);
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
