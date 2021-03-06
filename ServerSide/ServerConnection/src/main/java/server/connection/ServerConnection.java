package server.connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.MessageFormat;

import org.apache.log4j.Logger;

import assistant.connection.Connection;
import assistant.connection.ConnectionInfoPack;
import assistant.i18n.ResourceBundleHandler;
import server.persistence.PersistenceHandler;

/**
 * {@link ServerConnection}
 * 
 * @author costi.dumitrescu
 */
public class ServerConnection extends Connection {

	/**
	 * Logger for logging.
	 */
	private Logger logger = Logger.getLogger(Connection.class);
	
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
		
		// Try to create the server socket.
		this.establishConnection();
		
		// Start the thread that persists things in Data Base.
		PersistenceHandler.getInstance().start();
		
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
				
				String message = ResourceBundleHandler.getInstance().getResourceBundle().getString("ErrorOccurredWhenWaitingForAConnection");
				
				// Failed.
				this.logger.error(message, e);

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

				// {@link ServerHandlerThread} that is about to handle the new client.
				ServerHandlerThread client = new ServerHandlerThread(socket);

				// Save it in the ArrayList.
				ServerRoom.getInstance().addClient(client);

				// Start the thread.
				client.start();

			} catch (IOException e) {

				String message = ResourceBundleHandler.getInstance().getResourceBundle().getString("ErrorOccurredWhenCreatingTheStreamsOrTheSocketIsNotConnected");
				
				// Failed.
				this.logger.error(message, e);

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
			
			// Stop the thread that persists things in Data Base.
			PersistenceHandler.getInstance().stop();

			// Mock-up a connection to the server as a client so the thread that
			// is waiting for a new socket will pass the accept method, 
			// "Socket socket = serverSocket.accept();", and now the next 
			// statement is the checking for the 'keepGoing'.
			// As the 'keepGoing' is now set to false, it will exit.
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
		
		String message = MessageFormat.format(
				ResourceBundleHandler.getInstance().getResourceBundle().getString("ServerWaitingForClientsOnPort"),
				this.connectionInfoPack.getPortNumber());
		
		this.logger.warn(message);
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
		String message = ResourceBundleHandler.getInstance().getResourceBundle().getString("ServerWillShutDown");
		this.logger.warn(message);
		
		/*
		 * 
		 * Server is shouted down
		 * 
		 */

		ServerRoom.getInstance().removeAllClients();
		this.serverSocket.close();
	}
}
