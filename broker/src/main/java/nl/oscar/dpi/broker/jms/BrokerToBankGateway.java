package nl.oscar.dpi.broker.jms;

import nl.oscar.dpi.library.QueueNames;
import nl.oscar.dpi.library.jms.JmsGateway;
import nl.oscar.dpi.library.model.bank.BankInterestReply;
import nl.oscar.dpi.library.model.bank.BankInterestRequest;

public class BrokerToBankGateway extends JmsGateway<BankInterestRequest, BankInterestReply> {
    public BrokerToBankGateway() {
        super(QueueNames.INTEREST_REQUEST, QueueNames.INTEREST_REPLY);
    }
}
