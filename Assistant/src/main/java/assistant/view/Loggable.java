package assistant.view;

/**
 * {@link Loggable} interface to be passed to a connection for logging messages.
 * 
 * @author Costi.Dumitrescu
 *
 */
public interface Loggable {

	/**
	 * Log a message.
	 * 
	 * @param message The message to be logged.
	 */
	void logMessage(String message);
}
