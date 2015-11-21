package assistant.view;

/**
 * Loggable interface to be passed to a connection for logging messages.
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
	
	/**
	 * Log an error message, either as an error or a simply message.
	 * 
	 * @param errorMessage The error message to be logged.
	 */
	void logErrorMessage(String errorMessage);
}
