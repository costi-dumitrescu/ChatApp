package server.connection;

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
import assistant.message.arrivals.LoginMessagesRoom;
import assistant.message.arrivals.LogoutMessagesRoom;
import assistant.message.arrivals.WhoisinMessagesRoom;

/**
 * {@link ServerHandlerThread} is a {@link HandlerThread} with additional tasks. 
 */
public class ServerHandlerThread extends HandlerThread {

	/**
	 * Logger for logging.
	 */
	private Logger logger = Logger.getLogger(ServerHandlerThread.class);
	
	/**
	 * Constructor
	 * 
	 * @param socket   The socket to read and write to.
	 */
	public ServerHandlerThread(Socket socket) throws IOException {
		// Delegate to super constructor.
		super(socket, null);
	}

	/**
	 * @see java.lang.Thread.run()
	 */
	@Override
	public void run() {
		
		// WORK HARD.
		try {
			// Loop until the condition is no longer met. 
			while (ServerHandlerThread.this.isConnectionOpened) {
				ChatMessage message = (ChatMessage) this.objectInputStream.readObject();
				// Ask the {@link MessageHandler} to handle the message.
				MessageHandler.getInstance().handleMessage(this, message);
			}
		} catch (DOMException | ClassNotFoundException | IOException | ParserConfigurationException e) {
			ServerHandlerThread.this.logger.error("Exception in client thread : " + this.hashCode(), e);
			// The handler thread has terminated.
			// It has to remove himself from the room of clients.
			try {
				ServerRoom.getInstance().removeClient(ServerHandlerThread.this);
			} catch (IOException ioe) {
				ServerHandlerThread.this.logger.error("Exception removing client : " + this.hashCode(), ioe);
			}
		}
	}

	/**
	 * @see assistant.handler.Handler.handleLogin(String, String)
	 */
	public void handleLogin(String user, String message) throws IOException, ParserConfigurationException {
		
		// Log the message - Give a sign a login message has arrived.
		synchronized (LoginMessagesRoom.getInstance()) {
			LoginMessagesRoom.getInstance().addMessage(user + Messages.SEPARATOR + message);
			LoginMessagesRoom.getInstance().notify();
		}
		
		// As this is the server side, and moreover this is the first chat between both sides, this handler doesn't know the name of the user.
		// After the client has been accepted, save it's name.
		this.setUser(user);
		
		
		/*
		 * 
		 * #1 
		 * Broadcast the message to all others, to say 'user : logged in'
		 * 
		 */
		ChatMessage loginMessage = MessageHandler.getInstance().createMessage(MessageType.LOGIN, user, message);
		ServerRoom.getInstance().broadcast(loginMessage);
		
		
		/*
		 * 
		 * #2 
		 * Send a WHOISIN message to all clients, to update the list of users with this new one.
		 * 
		 */
		ChatMessage whoisinMessage = MessageHandler.getInstance().createMessage(MessageType.WHOISIN, user, ServerRoom.getInstance().listAllClients().toString());
		ServerRoom.getInstance().broadcast(whoisinMessage);
	}

	/**
	 * @see assistant.handler.Handler.handleWhoIsIn(String, String)
	 */
	public void handleWhoIsIn(String user, String message) throws IOException, ParserConfigurationException {
		
		// Log the message - Give a sign a who-is-in message has arrived.
		synchronized (WhoisinMessagesRoom.getInstance()) {
			WhoisinMessagesRoom.getInstance().addMessage(message);
			WhoisinMessagesRoom.getInstance().notify();
		}
		
		/*
		 * 
		 * #1 
		 * Should send the list of clients to the current asking client.
		 * 
		 */
		ChatMessage whoisinMessage = MessageHandler.getInstance().createMessage(MessageType.WHOISIN, user, ServerRoom.getInstance().listAllClients().toString());
		ServerRoom.getInstance().broadcast(whoisinMessage);
	}

	/**
	 * @see assistant.handler.Handler.handleMessage(String, String)
	 */
	public void handleMessage(String user, String message) throws IOException, ParserConfigurationException {
		
		// We shouldn't log the message on the screen. This is just a regularly message.
		
		
		/*
		 * 
		 * #1
		 * Broadcast the message to all clients.
		 * 
		 */
		ChatMessage chatMessage = MessageHandler.getInstance().createMessage(MessageType.MESSAGE, user, message);
		ServerRoom.getInstance().broadcast(chatMessage);
	}

	/**
	 * @see assistant.handler.Handler.handleLogout(String, String)
	 */
	public void handleLogout(String user, String message) throws IOException, ParserConfigurationException {

		// Log the message - Give a sign a logout message has arrived.
		synchronized (LogoutMessagesRoom.getInstance()) {
			LogoutMessagesRoom.getInstance().addMessage(user + Messages.SEPARATOR + message);
			LogoutMessagesRoom.getInstance().notify();
		}
		
		// Stop this handler thread.
		this.stopClient();

		/*
		 * 
		 * #1 
		 * Should broadcast the message to all others, to say 'user : logged out'
		 *
		 */
		ChatMessage logoutMessage = MessageHandler.getInstance().createMessage(MessageType.LOGOUT, user, message);
		ServerRoom.getInstance().broadcast(logoutMessage);
		
		
		/*
		 * 
		 * #2
		 * Should send a WHOISIN message to all others to update the list of users .
		 * 
		 */
		ChatMessage whoisinMessage = MessageHandler.getInstance().createMessage(MessageType.WHOISIN, user, ServerRoom.getInstance().listAllClients().toString());
		ServerRoom.getInstance().broadcast(whoisinMessage);
	}
}