package assistant.connection;

/**
 * Connection Handler. The top class in the connection hierarchy tree.
 * 
 * @author costi.dumitrescu
 */
public interface ConnectionHandler {

	/**
	 * 'Login' Message Type - specific behavior.
	 *
	 * @param listenerThread The listener thread.
	 * @param username       The user-name.
	 * @param message        The message.
	 */
	public void handleLogin(ListenerThread listenerThread, String username, String message);

	/**
	 * 'Who-is-in' Message Type - specific behavior.
	 * 
	 * @param listenerThread The listener thread.
	 * @param username       The user-name.
	 * @param message        The message.
	 */
	public void handleWhoisin(ListenerThread listenerThread, String username, String message);

	/**
	 * 'Message' Message Type - specific behavior.
	 * 
	 * @param listenerThread The listener thread.
	 * @param username       The user-name.
	 * @param message        The message.
	 */
	public void handleMessage(ListenerThread listenerThread, String username, String message);

	/**
	 * 'Login' Message Type - specific behavior.
	 * 
	 * @param listenerThread The listener thread.
	 * @param username       The user-name.
	 * @param message        The message.
	 */
	public void handleLogout(ListenerThread listenerThread, String username, String message);
}
