package nl.oscar.dpi.bank.frame;

import nl.oscar.dpi.library.QueueNames;
import nl.oscar.dpi.library.jms.receive.ApacheMqMessageReceiver;
import nl.oscar.dpi.library.jms.receive.MessageReceiver;
import nl.oscar.dpi.library.jms.receive.impl.ObjectMessageListener;
import nl.oscar.dpi.library.jms.send.MessageSender;
import nl.oscar.dpi.library.model.RequestReply;
import nl.oscar.dpi.library.model.bank.BankInterestReply;
import nl.oscar.dpi.library.model.bank.BankInterestRequest;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class JMSBankFrame extends JFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField tfReply;
	private DefaultListModel<RequestReply<BankInterestRequest, BankInterestReply>> listModel = new DefaultListModel<>();

	private Map<BankInterestRequest, String> origins = new HashMap<>();

	private MessageSender sender = new MessageSender(QueueNames.INTEREST_REPLY);
	private ApacheMqMessageReceiver receiver = new ApacheMqMessageReceiver(new MessageReceiver());

	/**
	 * Create the frame.
	 */
	public JMSBankFrame() {
		setTitle("JMS Bank - ABN AMRO");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
		gbc_scrollPane.gridwidth = 5;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		contentPane.add(scrollPane, gbc_scrollPane);

		JList<RequestReply<BankInterestRequest, BankInterestReply>> list = new JList<>(listModel);
		scrollPane.setViewportView(list);

		JLabel lblNewLabel = new JLabel("type reply");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 1;
		contentPane.add(lblNewLabel, gbc_lblNewLabel);

		tfReply = new JTextField();
		GridBagConstraints gbc_tfReply = new GridBagConstraints();
		gbc_tfReply.gridwidth = 2;
		gbc_tfReply.insets = new Insets(0, 0, 0, 5);
		gbc_tfReply.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfReply.gridx = 1;
		gbc_tfReply.gridy = 1;
		contentPane.add(tfReply, gbc_tfReply);
		tfReply.setColumns(10);

		JButton btnSendReply = new JButton("send reply");
		btnSendReply.addActionListener(e -> {
			RequestReply<BankInterestRequest, BankInterestReply> rr = list.getSelectedValue();
			if (rr != null) {
				double interest = Double.parseDouble((tfReply.getText()));
				BankInterestReply reply = new BankInterestReply(interest, "ABN AMRO");
				rr.setReply(reply);
				list.repaint();

				try {
					sender.sendObjectMessage(reply, origins.get(rr.getRequest()));
				} catch (JMSException e1) {
					System.out.println(e1.getMessage());
				}
			}
		});

		GridBagConstraints gbc_btnSendReply = new GridBagConstraints();
		gbc_btnSendReply.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnSendReply.gridx = 4;
		gbc_btnSendReply.gridy = 1;
		contentPane.add(btnSendReply, gbc_btnSendReply);
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JMSBankFrame frame = new JMSBankFrame();
					frame.initReceiver();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void initReceiver() {
		receiver.setListener(new ObjectMessageListener<BankInterestRequest>() {
			@Override
			protected void receivedObjectMessage(BankInterestRequest object, ObjectMessage message) {
				try {
					EventQueue.invokeLater(() -> listModel.addElement(new RequestReply<>(object, null)));
					origins.put(object, message.getJMSCorrelationID());
				} catch (JMSException e) {
					System.out.println(e.getMessage());
				}
			}
		});

		receiver.start(QueueNames.INTEREST_REQUEST);
	}

}
