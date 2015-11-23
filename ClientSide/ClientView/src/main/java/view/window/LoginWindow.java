package view.window;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
	 */
	public LoginWindow() {
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
		loginPanel.add(this.loginButton, BorderLayout.CENTER);
	}
	
	/**
	 * Returns the user-name.
	 * 
	 * @return the user-name.
	 */
	public String getUser() {
		return userTextField.getText();
	}

	/**
	 * Returns the server address.
	 * 
	 * @return the server address.
	 */
	public String getServerAddress() {
		return serverAddressTextField.getText();
	}
	
	/**
	 * Returns the port number.
	 * 
	 * @return the port number.
	 */
	public int getPortNumber() {
		return Integer.parseInt(portNumberTextField.getText());
	}
	
	/**
	 * Returns the login button.
	 * 
	 * @return the login button.
	 */
	public JButton getLoginButton() {
		return loginButton;
	}
	
	/**
	 * @see view.window.Window.log(String)
	 */
	@Override
	public void log(String message) {
		//#1 log4j
		logger.warn(message);
		
		//#2 dialog
		JOptionPane.showMessageDialog(null, message, "", JOptionPane.WARNING_MESSAGE);
	}
	
	/**
	 * @see view.window.Window.getLockableComponent()
	 */
	@Override
	public JComponent getLockableComponent() {
		// N/A
		return null;
	}
}
