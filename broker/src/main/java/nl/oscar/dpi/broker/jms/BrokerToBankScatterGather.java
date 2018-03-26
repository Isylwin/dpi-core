package nl.oscar.dpi.broker.jms;

import nl.oscar.dpi.library.jms.receive.impl.ObjectMessageListener;
import nl.oscar.dpi.library.model.BankName;
import nl.oscar.dpi.library.model.bank.BankInterestReply;
import nl.oscar.dpi.library.model.bank.BankInterestRequest;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import java.util.*;

public class BrokerToBankScatterGather {

    private Map<BankName, BrokerToBankGateway> gateways;
    private Map<String, Collection<ObjectMessage>> messages;

    public BrokerToBankScatterGather() {
        gateways = new HashMap<>();
        messages = new HashMap<>();

        Arrays.stream(BankName.values()).forEach(this::addGateway);
    }

    private void addGateway(BankName name) {
        gateways.put(name, new BrokerToBankGateway(name));
    }

    public void send(BankInterestRequest object, String corrId) {
        gateways.values().forEach(e -> e.send(object, corrId));
    }

    public void setListener(ObjectMessageListener<BankInterestReply> listener) {

        ObjectMessageListener<BankInterestReply> listener1 = new ObjectMessageListener<BankInterestReply>() {
            @Override
            protected void receivedObjectMessage(BankInterestReply object, ObjectMessage message) {
                try {
                    messages.putIfAbsent(message.getJMSCorrelationID(), new LinkedList<>());

                    Collection<ObjectMessage> replies = messages.get(message.getJMSCorrelationID());
                    replies.add(message);

                    if (replies.size() >= BankName.values().length) {
                        listener.receiveMessage(replies.stream().min(new Comparator<ObjectMessage>() {
                            @Override
                            public int compare(ObjectMessage o1, ObjectMessage o2) {
                                try {
                                    return Double.compare(((BankInterestReply) o1.getObject()).getInterest(), ((BankInterestReply) o2.getObject()).getInterest());
                                } catch (JMSException e) {
                                    return 0;
                                }
                            }
                        }).get());
                    }
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        };

        gateways.values().forEach(e -> e.addListener(listener1));
    }
}
