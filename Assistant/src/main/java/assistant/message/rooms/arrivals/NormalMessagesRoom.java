package assistant.message.rooms.arrivals;

/**
 * {@link NormalMessagesRoom}
 * 
 * @author Costi.Dumitrescu
 */
public class NormalMessagesRoom extends IncomingMessagesRoom {

	/**
	 * {@link LogoutMessagesRoom} Instance. Singleton purpose.
	 */
	protected static NormalMessagesRoom INSTANCE;
	
	/**
	 * Private constructor. Singleton purpose.
	 */
	private NormalMessagesRoom() {
	}

	/**
	 * Returns the single reference for the {@link NormalMessagesRoom} instance.
	 * Singleton purpose.
	 * 
	 * @return The single reference for the {@link NormalMessagesRoom} instance.
	 */
	public static synchronized NormalMessagesRoom getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new NormalMessagesRoom();
		}
		return INSTANCE;
	}
}
