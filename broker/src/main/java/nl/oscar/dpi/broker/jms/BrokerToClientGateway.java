package nl.oscar.dpi.broker.jms;

import nl.oscar.dpi.library.QueueNames;
import nl.oscar.dpi.library.jms.JmsGateway;
import nl.oscar.dpi.library.model.loan.LoanReply;
import nl.oscar.dpi.library.model.loan.LoanRequest;

public class BrokerToClientGateway extends JmsGateway<LoanReply, LoanRequest> {
    public BrokerToClientGateway() {
        super(QueueNames.LOAN_REPLY, QueueNames.LOAN_REQUEST);
    }
}
