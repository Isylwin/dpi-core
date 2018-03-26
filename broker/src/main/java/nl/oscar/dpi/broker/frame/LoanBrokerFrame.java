package nl.oscar.dpi.broker.frame;

import nl.oscar.dpi.broker.jms.BrokerToBankScatterGather;
import nl.oscar.dpi.broker.jms.BrokerToClientGateway;
import nl.oscar.dpi.library.jms.receive.impl.ObjectMessageListener;
import nl.oscar.dpi.library.model.bank.BankInterestReply;
import nl.oscar.dpi.library.model.bank.BankInterestRequest;
import nl.oscar.dpi.library.model.loan.LoanReply;
import nl.oscar.dpi.library.model.loan.LoanRequest;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


public class LoanBrokerFrame extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private DefaultListModel<JListLine> listModel = new DefaultListModel<JListLine>();
    private JList<JListLine> list;

    private BrokerToBankScatterGather brokerGateway = new BrokerToBankScatterGather();
    private BrokerToClientGateway clientGateway = new BrokerToClientGateway();

    /**
     * Create the frame.
     */
    public LoanBrokerFrame() {
        setTitle("Loan Broker");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        GridBagLayout gbl_contentPane = new GridBagLayout();
        gbl_contentPane.columnWidths = new int[]{46, 31, 86, 30, 89, 0};
        gbl_contentPane.rowHeights = new int[]{233, 23, 0};
        gbl_contentPane.columnWeights = new double[]{1.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_contentPane.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
        contentPane.setLayout(gbl_contentPane);

        JScrollPane scrollPane = new JScrollPane();
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.gridwidth = 7;
        gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 0;
        contentPane.add(scrollPane, gbc_scrollPane);

        list = new JList<>(listModel);
        scrollPane.setViewportView(list);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                LoanBrokerFrame frame = new LoanBrokerFrame();
                frame.initListener();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void initListener() {

        clientGateway.addListener(new ObjectMessageListener<LoanRequest>() {
            @Override
            protected void receivedObjectMessage(LoanRequest object, ObjectMessage message) {
                try {
                    BankInterestRequest request = new BankInterestRequest(object.getAmount(), object.getTime());
                    EventQueue.invokeLater(() -> {
                        add(object);
                        add(object, request);
                    });
                    brokerGateway.send(request, message.getJMSCorrelationID());
                } catch (JMSException e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        brokerGateway.setListener(new ObjectMessageListener<BankInterestReply>() {
            @Override
            protected void receivedObjectMessage(BankInterestReply object, ObjectMessage message) {
                try {
                    LoanReply reply = new LoanReply(object.getInterest(), object.getQuoteId());
                    EventQueue.invokeLater(() -> {
                        try {
                            add(message.getJMSCorrelationID(), object);
                        } catch (JMSException e) {
                            System.out.println(e.getMessage());
                        }
                    });
                    clientGateway.send(reply, message.getJMSCorrelationID());
                } catch (JMSException e) {
                    System.out.println(e.getMessage());
                }
            }
        });
    }

    private JListLine getRequestReply(LoanRequest request) {

        for (int i = 0; i < listModel.getSize(); i++) {
            JListLine rr = listModel.get(i);
            if (rr.getLoanRequest() == request) {
                return rr;
            }
        }

        return null;
    }

    private JListLine getRequestReply(String id) {

        for (int i = 0; i < listModel.getSize(); i++) {
            JListLine rr = listModel.get(i);
            if (rr.getLoanRequest().getUuid().toString().equals(id)) {
                return rr;
            }
        }

        return null;
    }

    public void add(LoanRequest loanRequest) {
        listModel.addElement(new JListLine(loanRequest));
    }


    public void add(LoanRequest loanRequest, BankInterestRequest bankRequest) {
        JListLine rr = getRequestReply(loanRequest);
        if (rr != null && bankRequest != null) {
            rr.setBankRequest(bankRequest);
            list.repaint();
        }
    }

    public void add(LoanRequest loanRequest, BankInterestReply bankReply) {
        JListLine rr = getRequestReply(loanRequest);
        if (rr != null && bankReply != null) {
            rr.setBankReply(bankReply);
            list.repaint();
        }
    }

    public void add(String loanRequestId, BankInterestReply bankReply) {
        JListLine rr = getRequestReply(loanRequestId);
        if (rr != null && bankReply != null) {
            rr.setBankReply(bankReply);
            list.repaint();
        }
    }


}
