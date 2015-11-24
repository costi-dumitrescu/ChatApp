package view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

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
public class ClientView extends View implements NotifiableView {
	
	/**
	 * The {@link Window} that is displayed on the {@link ClientView}
	 */
	private Window window;

	/**
	 * The {@link ConnectionInfoPack}
	 */
	private ConnectionInfoPack connectionInfoPack;

	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;
	
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
		this.setWindow(WindowType.LOGIN_WINDOW);
	}
	
	/**
	 * 
	 * 
	 * @see view.Notifiable.setWindow(windowType)
	 */
	@Override
	public void setWindow(WindowType windowType) {
		// Create a specific type of {@link Window}
		switch (windowType) {
			case LOGIN_WINDOW:
				final LoginWindow loginWindow = new LoginWindow(ClientView.this.connection, ClientView.this);
				// Activate the window.
				this.activate(loginWindow);
				break;
			case CHAT_WINDOW:
				final ChatWindow chatWindow = new ChatWindow(ClientView.this.connection, ClientView.this);
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
	
	/**
	 * @see view.NotifiableView.setConnectionInfoPack(ConnectionInfoPack)
	 */
	@Override
	public void setConnectionInfoPack(ConnectionInfoPack connectionInfoPack) {
		this.connectionInfoPack = connectionInfoPack;
	}
	
	/**
	 * @see view.NotifiableView.getConnectionInfoPack()
	 */
	@Override
	public ConnectionInfoPack getConnectionInfoPack() {
		return this.connectionInfoPack;
	}
}
