package connection;

import java.io.IOException;
import java.net.Socket;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.DOMException;

import assistant.handler.HandlerThread;
import assistant.message.ChatMessage;
import assistant.message.MessageHandler;
import assistant.message.MessageType;
import assistant.message.Messages;
import assistant.message.rooms.arrivals.LoginMessagesRoom;
import assistant.message.rooms.arrivals.LogoutMessagesRoom;
import assistant.message.rooms.arrivals.NormalMessagesRoom;
import assistant.message.rooms.arrivals.WhoisinMessagesRoom;
import assistant.message.rooms.departures.OutgoingMessagesRoom;

/**
 * {@link ClientHandlerThread} is a {@link HandlerThread} with additional tasks.
 * 
 * @author costi.dumitrescu
 */
public class ClientHandlerThread extends HandlerThread {

	/**
	 * Logger for logging.
	 */
	private Logger logger = Logger.getLogger(ClientHandlerThread.class);
	
	/**
	 * Constructor
	 * 
	 * @param socket   The {@link Socket} to read and write to.
	 * @param user	   The user.
	 */
	public ClientHandlerThread(Socket socket, String user ) throws IOException {
		// Delegate to super constructor.
		super(socket, user);
	}
	
	/**
	 * This thread handles the client. There are actually two threads started by
	 * this one. One of the two listens for messages from the server, and the
	 * other sends messages. Both get notified or notifies 3rd party messages
	 * room, and these rooms notify other threads that handles the information
	 * presented on the UI.
	 * 
	 * @see java.lang.Runnable.run()
	 */
	@Override
	public void run() {
		
		// Let the views to synchronize them self.
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			// Not much we can do.
		}
		
		/*
		 * Listener thread.
		 */
		new Thread("Listener-From-The-Server-Thread") {
			/**
			 * @see java.lang.Runnable.run()
			 */
			@Override
			public void run() {
				try {
					// Loop until the condition is no longer met. 
					while (ClientHandlerThread.this.isConnectionOpened) {
						ChatMessage chatMessage = (ChatMessage) ClientHandlerThread.this.objectInputStream.readObject();
						System.err.println(Thread.currentThread().getName() + " in execution RECEIVING message : " + chatMessage);
						// Ask the {@link MessageHandler} to handle the message.
						MessageHandler.getInstance().handleMessage(ClientHandlerThread.this, chatMessage);
					}
				} catch (DOMException | ClassNotFoundException | IOException | ParserConfigurationException e) {
					ClientHandlerThread.this.logger.error(Messages.EXCEPTION_IN_CLIENT_THREAD + this.hashCode(), e);
					// The handler thread has terminated.
					// It has to clear rubbish.
					try {
						ClientHandlerThread.this.stopClient();
					} catch (IOException ioe) {
						ClientHandlerThread.this.logger.error(Messages.EXCEPTION_IN_CLIENT_THREAD + this.hashCode(), ioe);
					}
				}
			};
		}.start();
		/*
		 *  Sender thread.
		 */
		new Thread("Sender-To-The-Server-Thread") {
			/**
			 * @see java.lang.Runnable.run()
			 */
			@Override
			public void run() {
				try {
					// Send LOGIN message.
					ChatMessage loginMessage = MessageHandler.getInstance().createMessage(MessageType.LOGIN, ClientHandlerThread.this.user, Messages.USER_IN);
					ClientHandlerThread.this.send(loginMessage);
				} catch (ParserConfigurationException | IOException e) {
					ClientHandlerThread.this.logger.error(Messages.EXCEPTION_IN_CLIENT_THREAD + this.hashCode(), e);
					// The handler thread has terminated.
					// It has to clear rubbish
					try {
						ClientHandlerThread.this.stopClient();
					} catch (IOException ioe) {
						ClientHandlerThread.this.logger.error(Messages.EXCEPTION_IN_CLIENT_THREAD + this.hashCode(), ioe);
					}
				}
				
				// Wait until a message needs to be sent, doesn't matter what
				// kind of message that is :
				// - login message	||
				// - normal message || => ChatMessage is a wrapper class for all kind of messages.
				// - logout message ||
				// - etc.
				
				// This thread will rise from the ashes whenever it gets notified by the lockable object.
				// Whenever someone notifies this thread, it  will send that chat message to the server.
				
				while (ClientHandlerThread.this.isConnectionOpened) {
					// The lockable object to acquire lock on.
					synchronized (OutgoingMessagesRoom.getInstance()) {
						try {
							OutgoingMessagesRoom.getInstance().wait();
						}  catch (InterruptedException e) {
							ClientHandlerThread.this.logger.error(Messages.EXCEPTION_IN_CLIENT_THREAD + this.hashCode(), e);
							continue;
						}
						// This thread could be released by mistake, so we have to check the size.
						if(OutgoingMessagesRoom.getInstance().getMessages().size() > 0) {
							// Send all messages to the server.
							for (ChatMessage chatMessage : OutgoingMessagesRoom.getInstance().getMessages()) {
								System.err.println(Thread.currentThread().getName() + " in execution SENDING message : " + chatMessage);
								try {
									ClientHandlerThread.this.send(chatMessage);
								} catch (IOException e) {
									ClientHandlerThread.this.logger.error(Messages.EXCEPTION_IN_CLIENT_THREAD + this.hashCode(), e);
									continue;
								}
							}
							// Remove all chat messages.
							OutgoingMessagesRoom.getInstance().getMessages().clear();
						}
					}
				}
			};
		}.start();
	}

	/**
	 * @see assistant.handler.Handler.handleLogin(String, String)
	 */
	public void handleLogin(String user, String message) {
		// Give a sign a login message has arrived.
		synchronized (LoginMessagesRoom.getInstance()) {
			LoginMessagesRoom.getInstance().addMessage(user + Messages.SEPARATOR + message);
			LoginMessagesRoom.getInstance().notify();
		}
	}

	/**
	 * @see assistant.handler.Handler.handleWhoIsIn(String, String)
	 */
	public void handleWhoIsIn(String user, String message) {
		// Give a sign a who-is-in message has arrived.
		synchronized (WhoisinMessagesRoom.getInstance()) {
			WhoisinMessagesRoom.getInstance().addMessage(message);
			WhoisinMessagesRoom.getInstance().notify();
		}
	}

	/**
	 * @see assistant.connection.ConnectionHandler.handleMessage()
	 */
	public void handleMessage(String user, String message) {
		// Give a sign a message has arrived.
		synchronized (NormalMessagesRoom.getInstance()) {
			NormalMessagesRoom.getInstance().addMessage(user + Messages.SEPARATOR + message);
			NormalMessagesRoom.getInstance().notify();
		}
	}

	/**
	 * @see assistant.connection.ConnectionHandler.handleLogout()
	 */
	public void handleLogout(String user, String message) {
		// Give a sign a logout message has arrived.
		synchronized (LogoutMessagesRoom.getInstance()) {
			LogoutMessagesRoom.getInstance().addMessage(user + Messages.SEPARATOR + message);
			LogoutMessagesRoom.getInstance().notify();
		}
	}
}