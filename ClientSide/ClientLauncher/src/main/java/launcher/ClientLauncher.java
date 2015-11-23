package launcher;

import assistant.connection.Connection;
import assistant.frame.Frame;
import assistant.view.View;
import connection.ClientConnection;
import view.ClientView;

/**
 * The main class to lunch the application.
 * 
 * @author costi.dumitrescu
 */
public class ClientLauncher {

	/**
	 * Main method.
	 * 
	 * @param args The arguments.
	 */
	public static void main(String[] args) {

		/*
		 * 
		 * Launch the client.
		 * 
		 */

		// The {@link ClientConnection}
		Connection clientConnection = new ClientConnection();

		// The {@link ClientView}
		View clientView = new ClientView(clientConnection);

		// The {@link Frame}
		Frame frame = new Frame(clientView);
		frame.setVisible(true);
	}
}
