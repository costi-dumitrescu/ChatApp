package assistant.message;

import java.io.Serializable;

import org.w3c.dom.Document;

/**
 * Chat message. A wrapper for an XML DOM {@link Document}.
 * 
 * @author costi.dumitrescu
 */
public class ChatMessage implements Serializable {

	/**
	 * Default serial version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The {@link Document} message.
	 */
	private Document message;

	/**
	 * Constructor.
	 * 
	 * @param message The {@link Document} message.
	 */
	public ChatMessage(Document message) {
		this.message = message;
	}

	/**
	 * Returns the {@link Document} message.
	 * 
	 * @return the {@link Document} message.
	 */
	public Document getMessage() {
		return this.message;
	}
}
