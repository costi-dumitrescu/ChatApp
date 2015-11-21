package server.connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import assistant.connection.Connection;
import assistant.connection.ConnectionInfoPack;
import assistant.connection.ListenerThread;
import assistant.message.MessageType;



/**
 * Server side connection.
 * 
 * @author costi.dumitrescu
 */
public class ServerConnection extends Connection {

	/**
	 * Logger for logging
	 */
	final static Logger logger = Logger.getLogger(ServerConnection.class);

	/**
	 * The socket used by the server. A server socket waits for requests to come
	 * in over the network. It performs some operation based on that request,
	 * and then possibly returns a result to the requester.
	 */
	private ServerSocket serverSocket;

	/**
	 * While this variable is <code>true</code> the server will be waiting for
	 * clients.
	 */
	private boolean isConnectionOpened;

	/**
	 * The list of all the Clients that are connected to this server.
	 */
	private ArrayList<ServerListenerThread> clients;

	/**
	 * Constructor.
	 */
	public ServerConnection() {
	}

	/**
	 * @see assistant.connection.Connection.start(ConnectionInfoPack)
     *
     * @param connectionInfoPack 	The server connection information.
     *
	 * @throws InterruptedException If any thread has interrupted the current thread. The
	 *				                interrupted status of the current thread is cleared when this
	 *				                exception is thrown.
	 * @throws IOException 			If an I/O error occurs when opening the socket, or 
	 * 								if an I/O error has occurred while closing the connections.
	 */
	public void start(ConnectionInfoPack connectionInfoPack) throws IOException, InterruptedException {
		
		// Set the connection information.
		this.connectionInfoPack = connectionInfoPack;
		
		// #1 Try to create the server socket.
		this.establishConnection();

		// Go.
		this.isConnectionOpened = true;

		// Loop to wait for connections until the connection is no longer opened.
		while (this.isConnectionOpened) {

			// The next client's socket.
			Socket socket = null;

			// #2
			try {

				// Listens for a connection to be made to this socket and
				// accepts it. The method blocks until a connection is made. 
				socket = this.serverSocket.accept();
				
			} catch (IOException e) {
				
				// Failed.
				logger.error("Error occured when waiting for a connection" , e);

				// Let this one down but keep on with others.
				continue;
				
			} 

			// Shout the server if the connection is no longer opened.
			if(!this.isConnectionOpened) {
				// bye-bye.
				this.stopServer();
				break;
			}

			try {

				// Thread that is about to handle the new client.
				ServerListenerThread ct = new ServerListenerThread(socket);

				// Save it in the ArrayList.
				this.clients.add(ct);

				// Start the thread.
				ct.start();

			} catch (IOException e) {

				// Failed.
				logger.error("Error occured when creating the streams or the socket is not connected " , e);

				// Let this one down but keep on with others.
				continue;
			}
		}
	}
	
	/**
	 * @see assistant.connection.Connection.stop()
	 * 
	 * @throws IOException if an I/O error occurs when creating the socket or, 
	 * 					   if the IP address of the host could not be determined.
	 */
	public void stop() throws IOException {

		// Allow user operation only if a connection has already been
		// established.
		if (this.serverSocket != null && this.connectionInfoPack != null) {
			
			// Connection will be closed. The loop will be stopped.
			this.isConnectionOpened = false;
			
			// Mock-up connect to the server as a client so the thread that is
			// waiting for a new socket will pass
			// the accept method, "Socket socket = serverSocket.accept();",
			// and now the next statement is the checking for the 'keepGoing'. As
			// keep going is now set to false, it will exit.
			new Socket("localhost", this.connectionInfoPack.getPortNumber());
		}
	}

	/**
	 * Tries to to create the server socket. If the port number is already
	 * occupied, then the connection will be interrupted.
	 * 
	 * @throws IOException If an I/O error occurs when opening the socket.
	 */
	private void establishConnection() throws IOException {
		this.serverSocket = new ServerSocket(this.connectionInfoPack.getPortNumber());
		logger.info("Server waiting for Clients on port " + this.connectionInfoPack.getPortNumber());
	}

	/**
	 * Shut all clients.The server is going down.The server thread has to wait
	 * for all client threads to close their connections.
	 * 
	 * @throws IOException 			If an I/O error has occurred while closing the connections.
	 * @throws InterruptedException If any thread has interrupted the current thread. The
	 *				                interrupted status of the current thread is cleared when this
	 *				                exception is thrown.
	 */
	private void destroyClients() throws IOException, InterruptedException {
		for (ServerListenerThread clientThread : this.clients) {
			clientThread.closeConnections();
			clientThread.join();
		}
	}
	
	/**
	 * 
	 * @throws InterruptedException If any thread has interrupted the current thread. The
	 *				                interrupted status of the current thread is cleared when this
	 *				                exception is thrown.
	 * @throws IOException 			If an I/O error occurs when opening the socket, or 
	 * 								if an I/O error has occurred while closing the connections.
	 */
	private void stopServer() throws IOException, InterruptedException {
		// bye
		logger.info("Server will shut down.");
		
		/*
		 * 
		 * Server is shouted down
		 * 
		 */

		this.destroyClients();
		this.serverSocket.close();
	}
	
	/**
	 * @see assistant.connection.ConnectionHandler.handleLogin(ListenerThread, String, String)
	 */
	public synchronized void handleLogin(ListenerThread listenerThread, String username, String message) {

		// Log the message.
		this.view.logMessage(username + " " + message);

		// Give to the current listener thread the name of the user.
		listenerThread.setUsername(username);

		/*
		 * 
		 * Send a WHOISIN message to update the list of users for each client.
		 * 
		 */

		// To keep all users.
		StringBuilder users = new StringBuilder();

		// Loop through the list of clients and let them all know.
		for (int i = 0; i < this.clients.size(); i++) {

			// Client.
			ServerListenerThread ct = this.clients.get(i);

			// User-name.
			users.append(ct.getUsername());

			// Dash
			users.append("-");
		}

		// who-is-in message to be broadcasted.
		Document whoisinMessage = this.messageHandler.createMessage(MessageType.WHOISIN, null, users.toString());

		// Broadcast the who-is-in message
		this.broadcast(whoisinMessage);

		/*
		 * 
		 * Broadcast the message to all others, to say 'user : logged in'
		 * 
		 */

		// login message to be broadcasted.
		Document loginMessage = this.messageHandler.createMessage(MessageType.LOGIN, username, message);

		// Broadcast the login message
		this.broadcast(loginMessage);
	}

	/**
	 * @see assistant.connection.ConnectionHandler.handleWhoisin(ListenerThread,
	 *      String, String)
	 */
	public synchronized void handleWhoisin(ListenerThread listenerThread, String username, String message) {

		// Log the message.
		this.view.logMessage(username + " " + message);

		// To keep all users.
		StringBuilder users = new StringBuilder();

		// Loop through the list of clients and let them all know.
		for (int i = 0; i < this.clients.size(); i++) {

			// Client.
			ServerListenerThread ct = this.clients.get(i);

			// User-name.
			users.append(ct.getUsername());

			// Dash
			users.append("-");
		}

		// who-is-in message to be broadcasted.
		Document whoisinMessage = this.messageHandler.createMessage(MessageType.WHOISIN, username, users.toString());

		// Send the list to the client.
		try {
			listenerThread.writeMessage(whoisinMessage);
		} catch (IOException e) {
			// If an error occurs, abort
			this.view.logMessage("Error sending the message : " + e + ".");
			// Shut down everything about this client.
			listenerThread.closeConnections();
		}
	}

	/**
	 * @see assistant.connection.ConnectionHandler.handleMessage(ListenerThread,
	 *      String, String)
	 */
	public synchronized void handleMessage(ListenerThread listenerThread, String username, String message) {
		// The XML message.
		Document xmlMessage = this.messageHandler.createMessage(MessageType.MESSAGE, username, message);
		// Broadcast.
		this.broadcast(xmlMessage);
	}

	/**
	 * @see assistant.connection.ConnectionHandler.handleLogout(ListenerThread,
	 *      String, String)
	 */
	public synchronized void handleLogout(ListenerThread listenerThread, String username, String message) {

		// Log the message.
		this.view.logMessage(username + " " + message);

		// Shut the mother-f... down
		listenerThread.closeConnections();

		/*
		 * 
		 * Send a WHOISIN message to update the list of users for each client.
		 * 
		 */

		// To keep all users.
		StringBuilder users = new StringBuilder();

		// Loop through the list of clients and let them all know.
		for (int i = 0; i < this.clients.size(); i++) {

			// Client.
			ServerListenerThread ct = this.clients.get(i);

			// User-name.
			users.append(ct.getUsername());

			// Dash
			users.append("-");
		}

		// who-is-in message to be broadcasted.
		Document whoisinMessage = this.messageHandler.createMessage(MessageType.WHOISIN, null, users.toString());

		// Broadcast the who-is-in message
		this.broadcast(whoisinMessage);

		/*
		 * 
		 * Broadcast the message to all others, to say 'user : logged in'
		 * 
		 */

		// logout message to be broadcasted, pay attention here.
		Document logoutMessage = this.messageHandler.createMessage(MessageType.LOGOUT, username, message);

		// Broadcast the login message
		this.broadcast(logoutMessage);
	}

	/**
	 * Broadcast a message to all Clients.
	 * 
	 * @param xmlMessage
	 *            The message to be broadcasted to all clients.
	 */
	public synchronized void broadcast(Document xmlMessage) {
		// Loop through the list of clients and let them all know.
		for (int i = 0; i < this.clients.size(); i++) {

			// Client.
			ServerListenerThread ct = this.clients.get(i);

			// Send the message this client.
			try {
				ct.writeMessage(xmlMessage);
			} catch (IOException e) {
				// If an error occurs, abort
				ServerConnection.this.view.logMessage("Error sending the message : " + e + ".");
				// Shut down everything about this client.
				ct.closeConnections();
			}
		}
	}

	/**
	 * Remove a client from the list of clients.
	 * 
	 * @param clientThread Is the client thread to be removed.
	 */
	public synchronized void remove(ServerListenerThread clientThread) {
		// Remove this client thread from the list.
		this.clients.remove(clientThread);

		// WHen a user just got out.
		if (this.clients.size() > 0) {
			// To keep all users.
			StringBuilder users = new StringBuilder();

			// Loop through the list of clients and let them all know.
			for (int i = 0; i < this.clients.size(); i++) {

				// Client.
				ServerListenerThread ct = this.clients.get(i);

				// User-name.
				users.append(ct.getUsername());

				// Dash
				users.append("-");
			}

			// who-is-in message to be broadcasted.
			Document whoisinMessage = this.messageHandler.createMessage(MessageType.WHOISIN, null, users.toString());

			// Broadcast the who-is-in message
			this.broadcast(whoisinMessage);
		}
	}
}
