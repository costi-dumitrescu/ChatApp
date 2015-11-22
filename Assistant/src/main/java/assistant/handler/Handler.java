package assistant.handler;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

/**
 * {@link Handler}. Handles the different types of incoming/outgoing messages.
 * 
 * @author costi.dumitrescu
 */
public interface Handler {

	/**
	 * 'Login' Message Type - specific behavior.
	 *
	 * @param username The user-name.
	 * @param message  The message.
	 * 
	 * @throws IOException 					Any exception thrown by the underlying OutputStream. 
	 * @throws ParserConfigurationException If a DocumentBuilder cannot be created which satisfies the configuration requested.
	 */
	public void handleLogin(String username, String message) throws IOException, ParserConfigurationException;

	/**
	 * 'Who-is-in' Message Type - specific behavior.
	 * 
	 * @param username The user-name.
	 * @param message  The message.
	 * 
	 * @throws IOException 					Any exception thrown by the underlying OutputStream. 
	 * @throws ParserConfigurationException If a DocumentBuilder cannot be created which satisfies the configuration requested.
	 */
	public void handleWhoIsIn(String username, String message) throws IOException, ParserConfigurationException;

	/**
	 * 'Message' Message Type - specific behavior.
	 * 
	 * @param username The user-name.
	 * @param message  The message.
	 * 
	 * @throws IOException 					Any exception thrown by the underlying OutputStream. 
	 * @throws ParserConfigurationException If a DocumentBuilder cannot be created which satisfies the configuration requested.
	 */
	public void handleMessage(String username, String message) throws IOException, ParserConfigurationException;

	/**
	 * 'Login' Message Type - specific behavior.
	 * 
	 * @param username The user-name.
	 * @param message  The message.
	 * 
	 * @throws IOException 					Any exception thrown by the underlying OutputStream. 
	 * @throws ParserConfigurationException If a DocumentBuilder cannot be created which satisfies the configuration requested.
	 */
	public void handleLogout(String username, String message) throws IOException, ParserConfigurationException;
}
