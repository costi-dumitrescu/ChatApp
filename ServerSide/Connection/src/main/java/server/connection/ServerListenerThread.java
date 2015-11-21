package server.connection;

import java.io.IOException;
import java.net.Socket;

import org.w3c.dom.Document;

import assistant.connection.ListenerThread;

/**
 * {@link ServerListenerThread} is {@link ListenerThread} with additional tasks. 
 */
public class ServerListenerThread extends ListenerThread {

	/**
	 * While this variable is <code>true</code> this {@link ListenerThread} will be
	 * looping.
	 */
	private boolean isConnectionOpened;
	
	/**
	 * Constructor
	 * 
	 * @param socket The socket to read and write to.
	 */
	public ServerListenerThread(Socket socket) throws IOException {
		// Delegate to super constructor.
		super(socket);
		
		// Go.
		this.isConnectionOpened = true;
	}

	/**
	 * @see java.lang.Thread.run()
	 */
	@Override
	public void run() {
		
		// Loop forever
		while (ServerListenerThread.this.isConnectionOpened) {
			try {
				// Read the message.
				Document message = (Document) objectInputStream.readObject();
				// Ask the handler to handle the message.
				ServerConnection.this.messageHandler.handleMessage(this, message);
				
			} catch (IOException e) {
				// Failed.
				ServerConnection.this.view.logMessage("Exception in client thread : " + this.hashCode() + " - " + e
						+ ". Client thread will be shuted");
				ServerConnection.this.handleLogout(this, this.username, "User has connection problems");
				break;
			} catch (ClassNotFoundException e) {
				// Failed.
				ServerConnection.this.view.logMessage("Exception in client thread : " + this.hashCode() + " - " + e
						+ ". Client thread will be shuted");
				this.closeConnections();
				break;
			}
		}
		// Shut down the thread.
		this.closeConnections();
	}

	/**
	 * @see assistant.connection.ListenerThread.closeConnections()
	 * 
	 * @throws IOException If an error has occurred while closing the connections.
	 */
	public void closeConnections() throws IOException {
		// Try to close everything first.
		super.closeConnections();

		// Remove myself from the arrayList containing the list of the
		// connected Clients
		ServerConnection.this.remove(this);
	}
}