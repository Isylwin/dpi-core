package nl.oscar.dpi.client.jms;

import nl.oscar.dpi.library.QueueNames;
import nl.oscar.dpi.library.jms.JmsGateway;
import nl.oscar.dpi.library.model.loan.LoanReply;
import nl.oscar.dpi.library.model.loan.LoanRequest;

public class ClientToBrokerGateway extends JmsGateway<LoanRequest, LoanReply> {

    public ClientToBrokerGateway() {
        super(QueueNames.LOAN_REQUEST, QueueNames.LOAN_REPLY);
    }
}
