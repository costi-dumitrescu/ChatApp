package assistant.message.rooms;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * {@link MessagesRoom}
 * 
 * @author Costi.Dumitrescu
 */
public abstract class MessagesRoom<T> {

	/**
	 * List with messages from one side, to be read on the other side.
	 * {@link ConcurrentLinkedQueue} would have been the best choice.
	 */
	protected List<T> messages;
	
	/**
	 * Constructor
	 */
	public MessagesRoom() {
		// Initialize the list of strings.
		this.messages = new ArrayList<>();
	}

	/**
	 * Add a message in the list.
	 * 
	 * @param message The message.
	 */
	public void addMessage(T message) {
		// Add the message in the list.`
		this.messages.add(message);
	}

	/**
	 * Returns the list with all messages.
	 * 
	 * @return the list with all messages.
	 */
	public List<T> getMessages() {
		return this.messages;
	}
}
