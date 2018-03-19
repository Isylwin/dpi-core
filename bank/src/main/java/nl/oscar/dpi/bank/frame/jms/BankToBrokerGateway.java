package nl.oscar.dpi.bank.frame.jms;

import nl.oscar.dpi.library.QueueNames;
import nl.oscar.dpi.library.jms.JmsGateway;
import nl.oscar.dpi.library.model.bank.BankInterestReply;
import nl.oscar.dpi.library.model.bank.BankInterestRequest;

public class BankToBrokerGateway extends JmsGateway<BankInterestReply, BankInterestRequest> {
    public BankToBrokerGateway() {
        super(QueueNames.INTEREST_REPLY, QueueNames.INTEREST_REQUEST);
    }
}
