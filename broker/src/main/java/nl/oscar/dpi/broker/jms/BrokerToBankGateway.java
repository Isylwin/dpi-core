package nl.oscar.dpi.broker.jms;

import nl.oscar.dpi.library.QueueNames;
import nl.oscar.dpi.library.jms.JmsGateway;
import nl.oscar.dpi.library.model.BankName;
import nl.oscar.dpi.library.model.bank.BankInterestReply;
import nl.oscar.dpi.library.model.bank.BankInterestRequest;

public class BrokerToBankGateway extends JmsGateway<BankInterestRequest, BankInterestReply> {
    public BrokerToBankGateway(BankName bankName) {
        super(QueueNames.INTEREST_REQUEST + "_" + bankName.name(), QueueNames.INTEREST_REPLY + "_" + bankName.name());
    }
}
