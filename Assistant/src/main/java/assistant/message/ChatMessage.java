package assistant.message;

import java.io.Serializable;

/**
 * Chat message.
 * 
 * @author costi.dumitrescu
 */
public class ChatMessage implements Serializable {

	/**
	 * Default serial version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The type of the message. One of 3 from above.
	 */
	private String type;

	/**
	 * The message.
	 */
	private String message;

	/**
	 * Constructor.
	 * 
	 * @param type    The type of the message.
	 * @param message The message.
	 */
	public ChatMessage(String type, String message) {
		this.type = type;
		this.message = message;
	}

	/**
	 * Returns the type.
	 * 
	 * @return the type.
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Returns the message.
	 * 
	 * @return the message.
	 */
	public String getMessage() {
		return this.message;
	}
}
