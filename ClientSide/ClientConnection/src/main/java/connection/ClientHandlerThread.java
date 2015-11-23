package connection;

import java.io.IOException;
import java.net.Socket;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;

import assistant.handler.HandlerThread;
import assistant.message.ChatMessage;
import assistant.message.MessageHandler;
import assistant.message.MessageType;
import assistant.message.Messages;
import assistant.view.Lockable;
import assistant.view.Loggable;

/**
 * {@link ClientHandlerThread} is a {@link HandlerThread} with additional tasks.
 * 
 * @author costi.dumitrescu
 */
public class ClientHandlerThread extends HandlerThread {

	/**
	 * Constructor
	 * 
	 * @param socket   The {@link Socket} to read and write to.
	 * @param loggable The {@link Loggable} instance.
	 * @param lockable The {@link Lockable} to get the object to acquire lock on.
	 * @param user	   The user.
	 */
	public ClientHandlerThread(Socket socket, Loggable loggable, Lockable lockable, String user ) throws IOException {
		// Delegate to super constructor.
		super(socket, loggable, lockable, user);
	}
	
	/**
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
		new Thread() {
			/**
			 * @see java.lang.Runnable.run()
			 */
			@Override
			public void run() {
				// WORK HARD.
				try {
					// Loop until the condition is no longer met. 
					while (ClientHandlerThread.this.isConnectionOpened) {
						ChatMessage message = (ChatMessage) ClientHandlerThread.this.objectInputStream.readObject();
						// Ask the {@link MessageHandler} to handle the message.
						MessageHandler.getInstance().handleMessage(ClientHandlerThread.this, message);
					}
				} catch (DOMException | ClassNotFoundException | IOException | ParserConfigurationException e) {
					ClientHandlerThread.this.loggable.logMessage(Messages.EXCEPTION_IN_CLIENT_THREAD + this.hashCode() + Messages.SEPARATOR + e);
					// The handler thread has terminated.
					// It has to clear rubbish.
					try {
						ClientHandlerThread.this.stopClient();
					} catch (IOException ioe) {
						ClientHandlerThread.this.loggable.logMessage(Messages.EXCEPTION_IN_CLIENT_THREAD + this.hashCode() + Messages.SEPARATOR + ioe);
					}
				}
			};
		}.start();
		/*
		 *  Sender thread.
		 */
		new Thread() {
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
					ClientHandlerThread.this.loggable.logMessage(Messages.EXCEPTION_IN_CLIENT_THREAD + this.hashCode() + Messages.SEPARATOR + e);
					// The handler thread has terminated.
					// It has to clear rubbish
					try {
						ClientHandlerThread.this.stopClient();
					} catch (IOException ioe) {
						ClientHandlerThread.this.loggable.logMessage(Messages.EXCEPTION_IN_CLIENT_THREAD + this.hashCode() + Messages.SEPARATOR + ioe);
					}
				}
				
				// Wait until a message needs to be sent, doesn't matter what
				// kind of message that is :
				// - normal message
				// - logout message
				// - etc.
				
				// This thread will rise from the ashes whenever it gets notified by the lockable object. In our case that's exactly the input text area.
				// Whenever someone hits the enter button in the input text area, this thread will send that text to the server.
				
				// The {@link Lockable} object to acquire lock on.
				Object lockableObject = ClientHandlerThread.this.lockable.getLockableObject();
				while (ClientHandlerThread.this.isConnectionOpened) {
					synchronized (lockableObject) {
						try {
							lockableObject.wait();
							ChatMessage message = MessageHandler.getInstance().createMessage(MessageType.MESSAGE, 
																							 ClientHandlerThread.this.user, 
																					 /*TODO*/"MUIE");
							ClientHandlerThread.this.send(message);
						} catch (InterruptedException | ParserConfigurationException | IOException e) {
							// The handler thread has terminated.
							// It has to clear rubbish
							try {
								ClientHandlerThread.this.stopClient();
							} catch (IOException ioe) {
								ClientHandlerThread.this.loggable.logMessage(Messages.EXCEPTION_IN_CLIENT_THREAD + this.hashCode() + Messages.SEPARATOR + ioe);
							}
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
		// Log the message.
		this.loggable.logMessage(user + Messages.SEPARATOR + message);
	}

	/**
	 * @see assistant.handler.Handler.handleWhoIsIn(String, String)
	 */
	public void handleWhoIsIn(String user, String message) {
		// Users. Special logging. 
		message = message + Messages.NEW_CLIENTS;
		this.loggable.logMessage(message);
	}

	/**
	 * @see assistant.connection.ConnectionHandler.handleMessage()
	 */
	public void handleMessage(String user, String message) {
		// Log the message.
		this.loggable.logMessage(user + Messages.SEPARATOR + message);
	}

	/**
	 * @see assistant.connection.ConnectionHandler.handleLogout()
	 */
	public void handleLogout(String user, String message) {
		// Log the message.
		this.loggable.logMessage(user + Messages.SEPARATOR + message);;
	}
}