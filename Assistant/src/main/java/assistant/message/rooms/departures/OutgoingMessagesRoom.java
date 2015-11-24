package assistant.message.rooms.departures;

import java.util.ArrayList;

import assistant.message.ChatMessage;
import assistant.message.rooms.MessagesRoom;
import assistant.message.rooms.arrivals.IncomingMessagesRoom;
import assistant.message.rooms.arrivals.LoginMessagesRoom;

/**
 * {@link OutgoingMessagesRoom}
 * 
 * @author Costi.Dumitrescu
 */
public class OutgoingMessagesRoom extends MessagesRoom<ChatMessage> {

	/**
	 * {@link IncomingMessagesRoom} Instance. Singleton purpose.
	 */
	protected static OutgoingMessagesRoom INSTANCE;

	/**
	 * Private constructor. Singleton purpose.
	 */
	private OutgoingMessagesRoom() {
		// Initialize the list of {@link ChatMessage}.
		this.messages = new ArrayList<>();
	}

	/**
	 * Returns the single reference for the {@link OutgoingMessagesRoom}
	 * instance. Singleton purpose.
	 * 
	 * @return The single reference for the {@link LoginMessagesRoom} instance.
	 */
	public static synchronized OutgoingMessagesRoom getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new OutgoingMessagesRoom();
		}
		return INSTANCE;
	}
}
