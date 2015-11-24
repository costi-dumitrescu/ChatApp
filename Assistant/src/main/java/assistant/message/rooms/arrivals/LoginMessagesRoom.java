package assistant.message.rooms.arrivals;

/**
 * {@link LoginMessagesRoom}
 * 
 * @author Costi.Dumitrescu
 */
public class LoginMessagesRoom extends IncomingMessagesRoom {

	/*
	 * 
	 * Short explanation.
	 * 
	 * If we would have had the 'LoginMessagesRoom INSTANCE' in the {@link MessagesRoom} class,
	 * because this is static, and the getInstence() method is also static, the lock would have
	 * been acquired on the class. That's way you had all that bugs before.
	 * As it is right now, each MessageRoom has it's own static instance, and the lock is acquired
	 * on each of them.
	 * 
	 * PAM-PAM
	 * 
	 * 
	 */
	
	/**
	 * {@link IncomingMessagesRoom} Instance. Singleton purpose.
	 */
	protected static LoginMessagesRoom INSTANCE;
	
	/**
	 * Private constructor. Singleton purpose.
	 */
	private LoginMessagesRoom() {
	}

	/**
	 * Returns the single reference for the {@link LoginMessagesRoom} instance.
	 * Singleton purpose.
	 * 
	 * @return The single reference for the {@link LoginMessagesRoom} instance.
	 */
	public static synchronized LoginMessagesRoom getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new LoginMessagesRoom();
		}
		return INSTANCE;
	}
}
