package assistant.connection;

import java.io.IOException;

/**
 * Abstract Connection.
 * 
 * @author costi.dumitrescu
 */
public abstract class Connection {

	/**
	 * The connection information.
	 */
	protected ConnectionInfoPack connectionInfoPack;

	/**
	 * Constructor.
	 */
	public Connection() {
	}

	/**
	 * Start the connection.
	 * 
	 * 
	 * @param connectionInfoPack	The information supplied for starting the connection.
	 * 
	 * @throws InterruptedException If any thread has interrupted the current thread. The
	 *				                interrupted status of the current thread is cleared when this
	 *				                exception is thrown.
	 * @throws IOException 			If an I/O error occurs when opening the socket, or 
	 * 								if an I/O error has occurred while closing the connections.
	 */
	public abstract void start(ConnectionInfoPack connectionInfoPack) throws IOException, InterruptedException;

	/**
	 * Stop the connection.
	 * 
	 * @throws IOException if an I/O error occurs when creating the socket or, 
	 * 					   if the IP address of the host could not be determined.
	 */
	public abstract void stop() throws IOException;
}
