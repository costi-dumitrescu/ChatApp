package launcher;

import assistant.connection.Connection;
import assistant.frame.Frame;
import assistant.view.View;
import server.connection.ServerConnection;
import view.ServerView;

/**
 * The main class to lunch the application.
 * 
 * @author costi.dumitrescu
 */
public class ServerLauncher {

	/**
	 * Main method.
	 * 
	 * @param args The arguments.
	 */
	public static void main(String[] args) {

		/*
		 * 
		 * Launch the server.
		 * 
		 */
		
		// {@link ServerConnection} instance
		Connection serverConnection = new ServerConnection();
		
		// {@link ServerView} instance
		View serverView = new ServerView(serverConnection);
		
		// {@link Frame} instance
		Frame frame = new Frame(serverView);
		frame.setVisible(true);
	}
}
