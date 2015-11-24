package view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import assistant.connection.Connection;
import assistant.connection.ConnectionInfoPack;
import assistant.message.arrivals.LoginMessagesRoom;
import assistant.message.arrivals.LogoutMessagesRoom;
import assistant.message.arrivals.WhoisinMessagesRoom;
import assistant.view.View;

/**
 * {@link ServerView}.
 * 
 * @author costi.dumitrescu
 */
public class ServerView extends View {

	/**
	 * Logger for logging
	 */
	final static Logger logger = Logger.getLogger(ServerView.class);
	
	/**
	 * Default serial version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The port number text field.
	 */
	private JTextField portNumberText;

	/**
	 * The log text area.
	 */
	private JTextArea logTextArea;

	/**
	 * The start button.
	 */
	private JButton startButton;
	
	/**
	 * The stop button.
	 */
	private JButton stopButton;
	
	/**
	 * <code>true</code> as long as the connection is opened.
	 */
	private boolean isConnectionOpened;

	/**
	 * Constructor.
	 * 
	 * @param connection The connection.
	 */
	public ServerView(Connection connection) {
		super(connection);
		// GO
		this.isConnectionOpened = true;
		
		// login messages listener thread.
		this.createListenerThreadForLoginMessages();
		
		// users listener thread.
		this.createListenerThreadForUsers();
		
		// logout messages listener thread.
		this.createListenerThreadForLogoutMessages();
	}

	/**
	 * @see assistant.view.View.initialize()
	 */
	@Override
	public void initialize() {

		// JPanel.
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		// Port Number
		final JLabel portNumberLabel = new JLabel("Port Number");
		portNumberLabel.setBounds(10, 40, 80, 25);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(2, 2, 2, 2);
		this.add(portNumberLabel, gbc);

		this.portNumberText = new JTextField(20);
		this.portNumberText.setBounds(100, 40, 160, 25);
		this.portNumberText.setText("1500");
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.gridx++;
		this.add(this.portNumberText, gbc);

		// Start button.
		this.startButton = new JButton("Start");
		this.startButton.addActionListener(new ActionListener() {
			/**
			 * @see java.awt.event.ActionListener.actionPerformed(ActionEvent)
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				// Start the connection.
				ServerView.this.startConnection();
			}
		});
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx++;
		this.add(this.startButton, gbc);

		// Stop button (not enabled by default).
		this.stopButton = new JButton("Stop");
		this.stopButton.setEnabled(false); 
		this.stopButton.addActionListener(new ActionListener() {
			/**
			 * @see java.awt.event.ActionListener.actionPerformed(ActionEvent)
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				// Stop the connection.
				ServerView.this.stopConnection();
			}
		});
		gbc.weightx = 0.0;
		gbc.gridx++;
		this.add(this.stopButton, gbc);

		// Chat text area.
		this.logTextArea = new JTextArea(30, 30);
		this.logTextArea.setLineWrap(true);
		this.logTextArea.setEditable(false);
		JScrollPane logScrollPane = new JScrollPane(this.logTextArea);
		logScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 4;
		this.add(logScrollPane, gbc);
	}

	/**
	 * Start the connection.
	 */
	private void startConnection() {
		// Create a new thread which handles the starting of the connection.
		new Thread() {
			/**
			 * @see java.lang.Runnable.run()
			 */
			@Override
			public void run() {
				// Disable the editable mode on the server view.
				ServerView.this.portNumberText.setEditable(false);
				ServerView.this.startButton.setEnabled(false);
				ServerView.this.stopButton.setEnabled(true);
				// Start the connection.
				try {
					ServerView.this.connection.start(ServerView.this.createTheInfoPack());
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	/**
	 * Stop the connection.
	 */
	private void stopConnection() {
		// Create a new thread which handles stopping of the connection.
		new Thread() {
			/**
			 * @see java.lang.Runnable.run()
			 */
			@Override
			public void run() {
				// No more editable.
				ServerView.this.portNumberText.setEditable(true);
				ServerView.this.startButton.setEnabled(true);
				ServerView.this.stopButton.setEnabled(false);
				// Stop the connection.
				try {
					ServerView.this.connection.stop();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	/**
	 * Create the information object to be passed to the connection, when the
	 * assistant.connection.Connection.start(ConnectionInfo) method is called.
	 * 
	 * @return The information object to be passed to the connection.
	 */
	private ConnectionInfoPack createTheInfoPack() {
		// Create an info pack.
		return new ConnectionInfoPack.ConnectionInfoPackBuilder().build(null, /* N/A */ 
																	    null, /* N/A - localhost */
																	    Integer.parseInt(this.portNumberText.getText())); 
	}
	
	/**
	 * This thread listens on the {@link LoginMessagesRoom} list and update the
	 * chat text area.
	 */
	private void createListenerThreadForLoginMessages() {
		// create a new thread to handle this.
		new Thread() {
			/**
			 * @see java.lang.Runnable.run()
			 */
			@Override
			public void run() {
				// forever.
				while (ServerView.this.isConnectionOpened) {
					// Synchronize on the list of messages.
					synchronized (LoginMessagesRoom.getInstance()) {
						try {
							LoginMessagesRoom.getInstance().wait();
						} catch (InterruptedException e) {
							// Not much we can do. Just continue.
							continue;
						}
						// This thread could be released by mistake, so we have to check the size.
						if(LoginMessagesRoom.getInstance().getMessages().size() > 0) {
							// Loop through all messages (even if at the list will have only one message at any time)
							for (String message : LoginMessagesRoom.getInstance().getMessages()) {
								// Append message in the chat text area.
								ServerView.this.logTextArea.append(message + "\n");
								// Put the caret at the end.
								ServerView.this.logTextArea.setCaretPosition(ServerView.this.logTextArea.getDocument().getLength());
							}
							// Remove all messages.
							LoginMessagesRoom.getInstance().getMessages().clear();
						}
					}
				}
			};
		}.start();
	}
	
	/**
	 * This thread listens on the {@link WhoisinMessagesRoom} list and update the
	 * users table.
	 */
	private void createListenerThreadForUsers() {
		// create a new thread to handle this.
		new Thread() {
			/**
			 * @see java.lang.Runnable.run()
			 */
			@Override
			public void run() {
				// forever.
				while (ServerView.this.isConnectionOpened) {
					// Synchronize on the list of messages.
					synchronized (WhoisinMessagesRoom.getInstance()) {
						try {
							WhoisinMessagesRoom.getInstance().wait();
						} catch (InterruptedException e) {
							// Not much we can do. Just continue.
							continue;
						}
						// This thread could be released by mistake, so we have to check the size.
						if(WhoisinMessagesRoom.getInstance().getMessages().size() > 0) {
							// Loop through all messages (even if at the list will have only one message at any time)
							for (String message : WhoisinMessagesRoom.getInstance().getMessages()) {
								// Append message in the chat text area.
								ServerView.this.logTextArea.append(message + "\n");
								// Put the caret at the end.
								ServerView.this.logTextArea.setCaretPosition(ServerView.this.logTextArea.getDocument().getLength());
							}
							// Remove all messages.
							WhoisinMessagesRoom.getInstance().getMessages().clear();
						}
					}
				}
			};
		}.start();
	}
	
	/**
	 * This thread listens on the {@link LogoutMessagesRoom} list and update the
	 * chat text area.
	 */
	private void createListenerThreadForLogoutMessages() {
		// create a new thread to handle this.
		new Thread() {
			/**
			 * @see java.lang.Runnable.run()
			 */
			@Override
			public void run() {
				// forever.
				while (ServerView.this.isConnectionOpened) {
					// Synchronize on the list of messages.
					synchronized (LogoutMessagesRoom.getInstance()) {
						try {
							LogoutMessagesRoom.getInstance().wait();
						} catch (InterruptedException e) {
							// Not much we can do. Just continue.
							continue;
						}
						// This thread could be released by mistake, so we have to check the size.
						if(LogoutMessagesRoom.getInstance().getMessages().size() > 0) {
							// Loop through all messages (even if at the list will have only one message at any time)
							for (String message : LogoutMessagesRoom.getInstance().getMessages()) {
								// Append message in the chat text area.
								ServerView.this.logTextArea.append(message + "\n");
								// Put the caret at the end.
								ServerView.this.logTextArea.setCaretPosition(ServerView.this.logTextArea.getDocument().getLength());
							}
							// Remove all messages.
							LogoutMessagesRoom.getInstance().getMessages().clear();
						}
					}
				}
			};
		}.start();
	}
}
