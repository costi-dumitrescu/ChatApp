package connection;

import java.io.IOException;
import java.net.Socket;

import org.w3c.dom.Document;

import assistant.connection.Connection;
import assistant.message.MessageType;

/**
 * Client side.
 * 
 * @author costi.dumitrescu
 */
public class ClientConnection extends Connection{

	/**
	 * The server address.
	 */
	private String serverAddress;

	/**
	 * The user-name.
	 */
	private String username;

	/**
	 * The listener thread.
	 */
	private ListenerThread listenerThread;

	/**
	 * Constructor.
	 * 
	 * @param clientConnectionInfo The client connection info.
	 */
	public ClientConnection(ClientConnectionInfo clientConnectionInfo) {
		super(clientConnectionInfo);
	}
	
	/**
	 * @see assistant.connection.Connection.start()
	 */
	public void start() throws ConnectionInterruptedException {

		// #1
		try {
			// Try to connect to the server.
			Socket socket = new Socket(this.serverAddress, this.portNumber);

			// Creates the Thread to listen from the server.
			this.listenerThread = new ListenFromServerThread(socket);
			
			// The user.
			this.listenerThread.setUsername(this.username);
			
			// HERE THE CLIENT IS ALREADY CONNECTED. SO, NOTHING ELSE TO PRINT.
			
		} catch (Exception e) {
			// Failed to connect.
			this.view.logMessage("Error connectiong to server:" + e + ".");
			// Break the process here.
			return ConnectionStatus.NOT_CONNTECTED;
		}

		// Message handler.
		this.adjustMessageHandler();
		
		// Success.
		// We inform the caller that it worked
		return ConnectionStatus.CONNECTED;
	}
	
	/**
	 * @see assistant.connection.Connection.stop()
	 */
	public void stop() throws ConnectionInterruptedException {
		// TODO
	}
	
	/**
	 * @see assistant.connection.ConnectionHandler.handleLogin(ListenerThread, String, String)
	 */
	public synchronized void handleLogin(ListenerThread listenerThread, String username, String message) {
		// Log the message on the view.
		this.view.logMessage(username + " : " + message);
	}

	/**
	 * @see assistant.connection.ConnectionHandler.handleWhoisin(ListenerThread, String, String)
	 */
	public synchronized void handleWhoisin(ListenerThread listenerThread, String username, String message) {
		// Users.
		String[] users = message.split("-");
		
		// Overkill---> keep it like this for now, anyway there is no danger.
		MainView mainView = (MainView) this.view;
		
		// Clear the list.
		mainView.clearUsersTable();
		
		// Recreate the list.
		for (int i = 0; i < users.length; i++) {
			mainView.appendUser(users[i]);
		}
	}

	/**
	 * @see assistant.connection.ConnectionHandler.handleMessage()
	 */
	public synchronized void handleMessage(ListenerThread listenerThread, String username, String message) {
		// Just log the message
		this.view.logMessage(username + " : " + message);
	}

	/**
	 * @see assistant.connection.ConnectionHandler.handleLogout()
	 */
	public synchronized void handleLogout(ListenerThread listenerThread, String username, String message) {
		// Just log the message
		this.view.logMessage(username + " : " + message);
	}
	
	/**
	 * Starts the listener thread, only if there is an established connection
	 * already.
	 * 
	 * @return The connection status. If not connected to the server, the
	 *         listener thread won't start.
	 */
	public ConnectionStatus startListenerThread() {
		// Check for null, which means there is no connection yet.
		if (this.listenerThread != null) {
			this.listenerThread.start();
			return ConnectionStatus.CONNECTED;
		} else {
			// No connection established at this point.
			return ConnectionStatus.NOT_CONNTECTED;
		}
	}

	/**
	 * Send message to the server.
	 * 
	 * @param message The message.
	 */
	public void writeMessage(String message) {
		// Avoid bad things.
		if (this.listenerThread != null) {
			// Create an XML message.
			Document xmlMessage = this.messageHandler.createMessage(MessageType.MESSAGE, this.listenerThread.getUsername(), message);
			try {
				// Send the message.
				this.listenerThread.writeMessage(xmlMessage);
			} catch (IOException e) {
				// If an error occurs, abort
				this.view.logMessage("Error sending the message : " + e + ".");
				// Shut down everything about this client.
				this.listenerThread.closeConnections();
			}
		}
	}

	/**
	 * Set the user-name.
	 * 
	 * @param username The user-name.
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * Set the server address.
	 * 
	 * @param serverAddress The server address.
	 */
	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}
	
	/**
	 * 
	 * A thread that waits for a message from the server, and logs this message
	 * in the view, but first two more additional purposes. First, tells the
	 * server that this user is logged in, and second, asks the server for
	 * others in the room.
	 * 
	 */
	public class ListenFromServerThread extends ListenerThread {
		
		/**
		 * Constructor
		 * 
		 * @param socket The socket to read and right to.
		 */
		public ListenFromServerThread(Socket socket) throws IOException {
			// Delegate to super constructor.
			super(socket);
		}
		
		/**
		 * @see java.lang.Runnable.run()
		 */
		@Override
		public void run() {

			// #1 - Before start, wait a second, so the views could be
			// synchronized.
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// Failed.
				ClientConnection.this.view.logMessage("Exception in client thread : " + e + ".");
				// Close everything.
				this.closeConnections();
				// Stop.
				return;
			}

			// Create a login message.
			Document loginMessage = ClientConnection.this.messageHandler.createMessage(MessageType.LOGIN, 
																					   ClientConnection.this.username,
																					   "User logged in.");

			// #2 - Send the login message, to notify the server that this user
			// has just logged in.
			try {
				this.writeMessage(loginMessage);
			} catch (IOException e) {
				// Log the message.
				ClientConnection.this.view.logMessage("Exception writing to server: " + e + ".");
				// Stop everything.
				this.closeConnections();
			}

			// Create a who-is-in message.
			Document whoIsInMessage = ClientConnection.this.messageHandler.createMessage("WHOISIN",
  																						 ClientConnection.this.username,
																						 "User asked who is in.");

			// #3 - Send the who-is-in message, to notify the server that this
			// user wants to see all others in the room.
			try {
				this.writeMessage(whoIsInMessage);
			} catch (IOException e) {
				// Log the message.
				ClientConnection.this.view.logMessage("Exception writing to server: " + e + ".");
				// Stop everything.
				this.closeConnections();
			}

			// #4 - Run this listener thread forever. It will wait for messages
			// from the server. Every time a new message is coming, the message
			// is displayed. If an exception occurs, just stop the thread.
			while (true) {
				try {
					// Read messages. 
					Document message = (Document) this.objectInputStream.readObject();
					// Ask the handler to handle the message.
					ClientConnection.this.messageHandler.handleMessage(this, message);
				} catch (IOException e) {
					// Failed.
					ClientConnection.this.view.logMessage("Server has close the connection: " + e + ".");
					// Break the thread.
					break;
				} catch (ClassNotFoundException e) {
					// Failed
					ClientConnection.this.view.logMessage("Connection lost: " + e + ".");
					// Break the thread.
					break;
				}
			}
			
			// Close everything.
			this.closeConnections();
		}
	}
}
