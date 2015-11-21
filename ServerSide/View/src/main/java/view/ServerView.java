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

import assistant.connection.Connection;
import assistant.connection.ConnectionInfoPack;
import assistant.view.View;

/**
 * Server view.
 * 
 * @author costi.dumitrescu
 */
public class ServerView extends View {

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
	 * Constructor.
	 * 
	 * @param connection The connection.
	 */
	public ServerView(Connection connection) {
		super(connection);
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
					// TODO Auto-generated catch block
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
					// TODO Auto-generated catch block
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
		return new ConnectionInfoPack.ConnectionInfoPackBuilder().build(/* N/A */ null, 
																	    /* N/A - localhost */ null, 
																	    Integer.parseInt(this.portNumberText.getText()), 
																	    this);
	}
	
	/**
	 * @see assistant.view.Loggable.logMessage(String)
	 */
	@Override 
	public void logMessage(String message) {
		// TODO
	}

	/**
	 * @see assistant.view.Loggable.logMessage(String)
	 */
	@Override
	public void logErrorMessage(String errorMessage) {
		// TODO
	}
}
