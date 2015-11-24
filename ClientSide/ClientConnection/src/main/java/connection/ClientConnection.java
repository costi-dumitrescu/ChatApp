package connection;

import java.io.IOException;
import java.net.Socket;

import assistant.connection.Connection;
import assistant.connection.ConnectionInfoPack;

/**
 * {@link ClientConnection}
 * 
 * @author costi.dumitrescu
 */
public class ClientConnection extends Connection{

	/**
	 * The listener thread.
	 */
	private ClientHandlerThread clientHandlerThread;

	/**
	 * Constructor.
	 */
	public ClientConnection() {
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

		/*
		 * Tries to connect to the server.. If the port number is already
		 * occupied, then the connection will be interrupted.
		 */
		Socket socket = new Socket(this.connectionInfoPack.getServerAddress(), this.connectionInfoPack.getPortNumber());

		// Creates the thread to listen from the server.
		this.clientHandlerThread = new ClientHandlerThread(socket, this.connectionInfoPack.getUser());
		
		// Start the thread.
		this.clientHandlerThread.start();
	}
	
	/**
	 * @see assistant.connection.Connection.stop()
	 * 
	 * @throws IOException if an I/O error occurs when creating the socket or, 
	 * 					   if the IP address of the host could not be determined.
	 */
	public void stop() throws IOException {
		// TODO - When a LOGOUT action occurs.
		this.clientHandlerThread.stopClient();
		// TODO - release the client from the input stream.
	}
}
