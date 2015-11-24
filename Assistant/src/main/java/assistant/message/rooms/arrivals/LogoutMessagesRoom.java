package assistant.message.rooms.arrivals;

/**
 * {@link LogoutMessagesRoom}
 * 
 * @author Costi.Dumitrescu
 */
public class LogoutMessagesRoom extends IncomingMessagesRoom {

	/**
	 * {@link LogoutMessagesRoom} Instance. Singleton purpose.
	 */
	protected static LogoutMessagesRoom INSTANCE;
	
	/**
	 * Private constructor. Singleton purpose.
	 */
	private LogoutMessagesRoom() {
	}

	/**
	 * Returns the single reference for the {@link LogoutMessagesRoom} instance.
	 * Singleton purpose.
	 * 
	 * @return The single reference for the {@link LogoutMessagesRoom} instance.
	 */
	public static synchronized LogoutMessagesRoom getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new LogoutMessagesRoom();
		}
		return INSTANCE;
	}
}
