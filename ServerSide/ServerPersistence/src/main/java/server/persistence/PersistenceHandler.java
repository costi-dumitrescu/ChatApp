package server.persistence;

import assistant.message.Messages;
import assistant.message.rooms.arrivals.NormalMessagesRoom;

/**
 * Thread that logs messages using {@link Persistence} singleton class.
 * 
 * @author costi.dumitrescu
 */
public class PersistenceHandler {

	/**
	 * Singleton instance.
	 */
	private static PersistenceHandler INSTANCE = null;
	
	/**
	 * <code>true</code> if the connection with the DATA BASE is opened.
	 */
	private boolean isConnectionOpened;

	/**
	 * Constructor.
	 */
	private PersistenceHandler() {
	}

	/**
	 * Returns the single reference for the
	 * {@link PersPersistenceHandleristence} instance. Singleton purpose.
	 * 
	 * @return The single reference for the {@link PersistenceHandler} instance.
	 */
	public static synchronized PersistenceHandler getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new PersistenceHandler();
		}
		return INSTANCE;
	}

	/**
	 * Stop the thread that persists things in Data Base.
	 */
	public void stop() {
		// Take the thread out of the loop.
		this.isConnectionOpened = false;
		
		/*
		 * 
		 *  TODO
		 *  
		 */
	}

	/**
	 * Start the thread that persists things in Data Base.
	 * 
	 * @return <code>true</code> if in the current session things could be
	 *         persisted in Data Base.
	 */
	public boolean start() {

		// engine status.
		boolean status = false;

		// Persistence singleton.
		Persistence instance = Persistence.getInstance();

		// If the connection has been established.
		if (instance != null) {
			status = true;
			// Allow the loop of the persistence handler
			this.isConnectionOpened = true;
			
			// Create new thread to handle the persistence
			new Thread("Persistence-Thread") {
				/**
				 * @see java.lang.Thread.run();
				 */
				@Override
				public void run() {
					while (PersistenceHandler.this.isConnectionOpened) {
						// Synchronize on the list of messages.
						synchronized (NormalMessagesRoom.getInstance()) {
							try {
								NormalMessagesRoom.getInstance().wait();
							} catch (InterruptedException e) {
								// Not much we can do. Just continue.
								continue;
							}
							// This thread could be released by mistake, so we
							// have to check the size.
							if (NormalMessagesRoom.getInstance().getMessages().size() > 0) {
								// Loop through all messages (even if at the
								// list will have only one message at any time)
								for (String message : NormalMessagesRoom.getInstance().getMessages()) {
									// user : message
									String[] splited = message.split(Messages.SEPARATOR);
									
									/**********************************************************************************************************/
									
									// Persistence.getInstance().insertMessageSimpleStatement(splited[0], splited[1], new Date().toString());
									// Persistence.getInstance().insertMessagePreparedStatement(splited[0], splited[1], new Date().toString());
									// Persistence.getInstance().insertMessageCallableStatement(splited[0], splited[1], new Date().toString());
									
									// Persistence.getInstance().displayHistorySimpleStatement();
									// Persistence.getInstance().displayHistoryPreparedStatement();
									Persistence.getInstance().displayHistoryCallableStatement();
									
									/**********************************************************************************************************/
									
								}	
								// Remove all messages.
								NormalMessagesRoom.getInstance().getMessages().clear();
							}
						}
					}
				}
			}.start();
		}
		return status;
	}
}
