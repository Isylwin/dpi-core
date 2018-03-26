package nl.oscar.dpi.bank.frame.jms;

import nl.oscar.dpi.library.QueueNames;
import nl.oscar.dpi.library.jms.JmsGateway;
import nl.oscar.dpi.library.model.BankName;
import nl.oscar.dpi.library.model.bank.BankInterestReply;
import nl.oscar.dpi.library.model.bank.BankInterestRequest;

public class BankToBrokerGateway extends JmsGateway<BankInterestReply, BankInterestRequest> {
    public BankToBrokerGateway(BankName bankName) {
        super(QueueNames.INTEREST_REPLY + "_" + bankName.name(), QueueNames.INTEREST_REQUEST + "_" + bankName.name());
    }
}
