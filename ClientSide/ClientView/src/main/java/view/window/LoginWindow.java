package view.window;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import assistant.connection.Connection;
import assistant.connection.ConnectionInfoPack;
import view.NotifiableView;

/**
 * The login view.
 * 
 * @author costi.dumitrescu
 */
public class LoginWindow extends Window {
	
	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The user-name label.
	 */
	private JLabel usernameLabel;
	
	/**
	 * The user-name text field.
	 */
	private JTextField userTextField;

	/**
	 * The server address label.
	 */
	private JLabel serverAddressLabel;
	
	/**
	 * The server address text field.
	 */
	private JTextField serverAddressTextField;
	
	/**
	 * The port number label.
	 */
	private JLabel portNumberLabel;
	
	/**
	 * The port number text field.
	 */
	private JTextField portNumberTextField;
	
	/**
	 * The login button.
	 */
	private JButton loginButton;
	
	/**
	 * Constructor.
	 * 
	 * @param connection The {@link Connection}
	 * @param notifiable The {@link NotifiableView}
	 */
	public LoginWindow(Connection connection, NotifiableView notifiable) {
		super(connection, notifiable);
	}

	/**
	 * @see view.window.Window.initialize()
	 */
	public void initialize() {
		
		// Login panel.
		this.setLayout(new GridBagLayout());

		// Constraints
		GridBagConstraints gbc = new GridBagConstraints();

		// Username
		this.usernameLabel = new JLabel("Username");
		this.usernameLabel.setBounds(10, 10, 80, 25);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(2, 2, 2, 2);
		this.add(this.usernameLabel, gbc);

		this.userTextField = new JTextField(20);
		this.userTextField.setBounds(100, 10, 160, 25);
		this.userTextField.setText("Anonymous");
		gbc.gridx++;
		this.add(this.userTextField, gbc);

		// Server Address
		this.serverAddressLabel = new JLabel("Server Address");
		this.serverAddressLabel.setBounds(10, 40, 80, 25);
		gbc.gridx = 0;
		gbc.gridy++;
		this.add(this.serverAddressLabel, gbc);

		this.serverAddressTextField = new JTextField(20);
		this.serverAddressTextField.setBounds(100, 40, 160, 25);
		this.serverAddressTextField.setText("localhost");
		gbc.gridx++;
		this.add(this.serverAddressTextField, gbc);

		// Port Number
		this.portNumberLabel = new JLabel("Port Number");
		this.portNumberLabel.setBounds(10, 40, 80, 25);
		gbc.gridx = 0;
		gbc.gridy++;
		this.add(this.portNumberLabel, gbc);

		this.portNumberTextField = new JTextField(20);
		this.portNumberTextField.setBounds(100, 40, 160, 25);
		this.portNumberTextField.setText("1500");
		gbc.gridx++;
		this.add(this.portNumberTextField, gbc);
		
		// Login Panel
		JPanel loginPanel = new JPanel();
		loginPanel.setLayout(new BorderLayout());
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		this.add(loginPanel, gbc);

		// Login Button
		this.loginButton = new JButton("Login");
		this.loginButton.setBounds(10, 80, 80, 25);
		this.loginButton.addActionListener(new ActionListener() {
			/**
			 * @see java.awt.event.ActionListener.actionPerformed(ActionEvent)
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				
				// Create the {@link ConnectionInfoPack} for this session.
				ConnectionInfoPack connectionInfoPack = LoginWindow.this.createConnectionInfoPack();
				// Start the connection, only if the pack has been created.
				if (connectionInfoPack != null) {
					try {
						LoginWindow.this.connection.start(connectionInfoPack);
						// If no exception has be thrown, it means the
						// connection has been established, and the {@link
						// ChatWindow} could be presented, and the pack could be saved.
						LoginWindow.this.notifiableView.setConnectionInfoPack(connectionInfoPack);
						LoginWindow.this.notifiableView.setWindow(WindowType.CHAT_WINDOW);
					} catch (IOException | InterruptedException ex) {
						// Not much we can do.
						JOptionPane.showMessageDialog(LoginWindow.this, ex.getLocalizedMessage());
					}

				}
			}
		});
		loginPanel.add(this.loginButton, BorderLayout.CENTER);
	}
	
	/**
	 * Returns the {@link ConnectionInfoPack} used by the connection, and also
	 * by the {@link ChatWindow}.
	 * 
	 * @return the {@link ConnectionInfoPack} used by the connection, and also
	 *         by the {@link ChatWindow}.
	 */
	private ConnectionInfoPack createConnectionInfoPack() {
		ConnectionInfoPack connectionInfoPack = null;
		if (this.userTextField != null && !"".equals(this.userTextField.getText()) && 
			this.serverAddressTextField != null && !"".equals(this.serverAddressTextField.getText()) && 
			this.portNumberTextField != null && !"".equals(this.portNumberTextField.getText())) {
			
			// Create the connection info pack.
			connectionInfoPack = new ConnectionInfoPack.ConnectionInfoPackBuilder().build(
					this.userTextField.getText(), 								// The user
					this.serverAddressTextField.getText(), 						// The server addressport
					Integer.parseInt(this.portNumberTextField.getText()));		// The port number
		}
		return connectionInfoPack;
	}
}
