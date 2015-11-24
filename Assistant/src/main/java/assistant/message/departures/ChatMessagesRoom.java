package assistant.message.departures;

import java.util.ArrayList;
import java.util.List;

import assistant.message.ChatMessage;
import assistant.message.arrivals.LoginMessagesRoom;
import assistant.message.arrivals.MessagesRoom;

/**
 * {@link ChatMessagesRoom}
 * 
 * @author Costi.Dumitrescu
 */
public class ChatMessagesRoom {

	/**
	 * {@link MessagesRoom} Instance. Singleton purpose.
	 */
	protected static ChatMessagesRoom INSTANCE;

	/**
	 * List with messages from one side, to be read on the other side.
	 */
	protected List<ChatMessage> messages;

	/**
	 * Private constructor. Singleton purpose.
	 */
	private ChatMessagesRoom() {
		// Initialize the list of {@link ChatMessage}.
		this.messages = new ArrayList<>();
	}

	/**
	 * Returns the single reference for the {@link ChatMessagesRoom} instance.
	 * Singleton purpose.
	 * 
	 * @return The single reference for the {@link LoginMessagesRoom} instance.
	 */
	public static synchronized ChatMessagesRoom getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ChatMessagesRoom();
		}
		return INSTANCE;
	}

	/**
	 * Add a message in the list.
	 * 
	 * @param message The message.
	 */
	public void addChatMessage(ChatMessage chatMessage) {
		// Add the message in the list and then
		// notify the one who is waiting for it.
		synchronized (this.messages) {
			this.messages.add(chatMessage);
			this.messages.notify();
		}
	}

	/**
	 * Returns the list with all messages.
	 * 
	 * @return the list with all messages.
	 */
	public List<ChatMessage> getChatMessages() {
		return this.messages;
	}
}
