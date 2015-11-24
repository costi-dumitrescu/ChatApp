package view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import assistant.connection.Connection;
import assistant.connection.ConnectionInfoPack;
import assistant.view.View;
import view.window.ChatWindow;
import view.window.LoginWindow;
import view.window.Window;
import view.window.WindowType;

/**
 * {@link ClientView}.
 * 
 * @author Costi.Dumitrescu
 */
public class ClientView extends View {
	
	/**
	 * The {@link Window} that is displayed in the {@link ClientView}
	 */
	private Window window;

	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * {@link Logger} for logging
	 */
	final static Logger logger = Logger.getLogger(ClientView.class);
	
	/**
	 * Constructor.
	 * 
	 * @param connection The {@link Connection} instance.
	 */
	public ClientView(Connection connection) {
		super(connection);
	}
	
	/**
	 * @see assistant.view.View.initialize()
	 */
	@Override
	public void initialize() {
		// Set the Grid Bag Layout.
		this.setLayout(new GridBagLayout());
		// Login is the first window a user has to pass through.
		this.initialize(WindowType.LOGIN_WINDOW);
	}
	
	/**
	 * Initialize a specific type of the {@link Window} to be presented on the {@link View}.
	 * 
	 * @param windowType The type of the {@link Window} to be presented on the {@link View}.
	 */
	private void initialize(WindowType windowType) {
		// Create a specific type of {@link Window}
		switch (windowType) {
			case LOGIN_WINDOW:
				final LoginWindow loginWindow = new LoginWindow();
				loginWindow.getLoginButton().addActionListener(new ActionListener() {
					/**
					 * @see java.awt.event.ActionListener.actionPerformed(ActionEvent)
					 */
					@Override
					public void actionPerformed(ActionEvent e) {
						// Create a connection info pack.
						ConnectionInfoPack connectionInfoPack = new ConnectionInfoPack.ConnectionInfoPackBuilder().build(
								loginWindow.getUser(), 				// The user
								loginWindow.getServerAddress(), 	// The server addressport
								loginWindow.getPortNumber());		// The port number

						// Start the connection.
						try {
							ClientView.this.connection.start(connectionInfoPack);
							// If no exception has be thrown, it means the
							// connection has been established, and the {@link ChatWindow}
							// could be presented.
							ClientView.this.initialize(WindowType.CHAT_WINDOW);
						} catch (IOException | InterruptedException ex) {
							// Not much we can do.
							JOptionPane.showMessageDialog(ClientView.this, ex.getLocalizedMessage());
						}
					}
				});
				// Activate the window.
				this.activate(loginWindow);
				break;
			case CHAT_WINDOW:
				final ChatWindow chatWindow = new ChatWindow();
				// Activate the window.
				this.activate(chatWindow);
				break;
			default:
				break;
		}
	}

	/**
	 * After a a {@link Window} has been changed, we need to repaint the panel
	 * and to validate the changes.
	 * 
	 * @param window The new {@link Window}.
	 */
	private void activate(Window window) {
		// Remove the old if any
		if (this.window != null) {
			this.remove(this.window);
		}
		// Set up the new one and add it.
		this.window = window;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 1;
		this.add(this.window, gbc);
		// Re-Re
		this.repaint();
		this.revalidate();
	}
}
