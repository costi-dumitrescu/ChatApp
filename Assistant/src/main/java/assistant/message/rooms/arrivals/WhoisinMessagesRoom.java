package assistant.message.rooms.arrivals;

/**
 * {@link WhoisinMessagesRoom}
 * 
 * @author Costi.Dumitrescu
 */
public class WhoisinMessagesRoom extends IncomingMessagesRoom {

	/**
	 * {@link WhoisinMessagesRoom} Instance. Singleton purpose.
	 */
	protected static WhoisinMessagesRoom INSTANCE;
	
	
	/**
	 * Private constructor. Singleton purpose.
	 */
	private WhoisinMessagesRoom() {
	}

	/**
	 * Returns the single reference for the {@link WhoisinMessagesRoom} instance.
	 * Singleton purpose.
	 * 
	 * @return The single reference for the {@link WhoisinMessagesRoom} instance.
	 */
	public static synchronized WhoisinMessagesRoom getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new WhoisinMessagesRoom();
		}
		return INSTANCE;
	}
}
