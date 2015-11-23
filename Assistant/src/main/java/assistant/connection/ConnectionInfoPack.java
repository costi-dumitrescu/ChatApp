package assistant.connection;

import assistant.view.Lockable;
import assistant.view.Loggable;

/**
 * A {@link Connection} can not be established/created without a few
 * information, like the port number to listen to.
 * 
 * @author Costi.Dumitrescu
 */
public class ConnectionInfoPack {
	
	/**
	 * Info pack builder.
	 * 
	 * @author Costi.Dumitrescu
	 */
	public static class ConnectionInfoPackBuilder {
		
		/**
		 * Build a connection information pack instance.
		 * 
		 * @param user          The user.
		 * @param serverAddress The server address
		 * @param portNumber 	The port number to listen to.
		 * @param loggable	 	The loggable to log messages.
		 * @param lockable		The lockable to get the object to acquire lock on.
		 * 
		 * @return Connection information pack instance.
		 */
		public ConnectionInfoPack build(String user, String serverAddress, int portNumber, Loggable loggable, Lockable lockable) {
			// Create an info pack instance.
			return new ConnectionInfoPack(user, 
										  serverAddress, 
										  portNumber, 
										  loggable,
										  lockable);
		}
	}
	
	/**
	 * The user. 
	 * 
	 * {Applicable only for client side}
	 */
	private String user;
	
	/**
	 * The server address.
	 * 
	 * {Applicable only for client side}
	 */
	private String serverAddress;
	
	/**
	 * The port number to listen to.
	 */
	private int portNumber;
	
	/**
	 * The loggable to log messages.
	 */
	private Loggable loggable;
	
	/**
	 * The lockable to get the object to acquire lock on.
	 */
	private Lockable lockable;
	
	/**
	 * Constructor.
	 * 
	 * @param user          The user.
	 * @param serverAddress The server address
	 * @param portNumber 	The port number to listen to.
	 * @param loggable	 	The loggable to log messages.
	 * @param lockable		The lockable to get the object to acquire lock on.
	 */
	private ConnectionInfoPack(String user, String serverAddress, int portNumber, Loggable loggable, Lockable lockable) {
		this.user 		   = user;
		this.serverAddress = serverAddress;
		this.portNumber    = portNumber;
		this.loggable      = loggable;
		this.lockable	   = lockable;
	}
	
	/**
	 * Returns the user.
	 * 
	 * @return the user.
	 */
	public String getUser() {
		return user;
	}
	
	/**
	 * Returns the server address.
	 * 
	 * @return the server address.
	 */
	public String getServerAddress() {
		return serverAddress;
	}
	
	/**
	 * Returns the port number.
	 * 
	 * @return the port number.
	 */
	public int getPortNumber() {
		return this.portNumber;
	}
	
	/**
	 * Returns the loggable.
	 * 
	 * @return the loggable.
	 */
	public Loggable getLoggable() {
		return loggable;
	}
	
	/**
	 * Returns the lockable to get the object to acquire lock on.
	 * 
	 * @return The lockable to get the object to acquire lock on.
	 */
	public Lockable getLockable() {
		return lockable;
	}
}
