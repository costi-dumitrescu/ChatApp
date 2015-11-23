package assistant.handler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import assistant.message.ChatMessage;
import assistant.view.Loggable;

/**
 * Each newly came client is handled by a {@link HandlerThread}.
 * 
 * @author costi.dumitrescu
 */
public abstract class HandlerThread extends Thread implements Handler {

	/**
	 * The {@link Socket} where to listen/write to.
	 * 
	 * A network socket is an end-point of an inter-process communication across
	 * a computer network. A socket address is the combination of an IP address
	 * and a port number. Internet sockets deliver incoming data packets to the
	 * appropriate application process or thread.
	 */
	protected Socket socket;
	
	/**
	 * To read from the {@link Socket}.
	 */
	protected ObjectInputStream objectInputStream;
	
	/**
	 * To write on the {@link Socket}.
	 */
	protected ObjectOutputStream objectOutputStream;
	
	/**
	 * The {@link Loggable} to log message to. 
	 */
	protected Loggable loggable;
	
	/**
	 * The client associated with this listener.
	 */
	protected String user;
	
	/**
	 * While this variable is <code>true</code> this {@link HandlerThread} will be
	 * looping.
	 */
	protected boolean isConnectionOpened;
	
	/**
	 * Constructor.
	 * 
	 * @param socket   The {@link Socket} to read and right to.
	 * @param loggable The {@link Loggable} to log message to. 
	 */
	public HandlerThread(Socket socket, Loggable loggable) throws IOException {
		this.socket 			= socket;
		this.loggable 		    = loggable;
		this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
		this.objectInputStream  = new ObjectInputStream(socket.getInputStream());
		this.isConnectionOpened = true;
	}
	
	/**
	 * Sets the user-name.
	 * 
	 * @param username The user-name.
	 */
	public void setUser(String username) {
		this.user = username;
	};
	
	/**
	 * Returns the user.
	 * 
	 * @return the user.
	 */
	public String getUser() {
		return user;
	};

	/**
	 * Stop this handler thread.
	 * 
	 * @throws IOException If an error has occurred while closing the connections.
	 */
	public void stopClient() throws IOException {
		
		// TODO - maybe to interrupt this thread instead of setting to false the isConnectionOpened variable.
		// 		- it could be blocked in the read method, and won't release until it reads something there.
				
		this.isConnectionOpened = false;
		this.closeConnections();
	}
	
	/**
	 * Shut down this thread. 
	 * Close everything.
	 * 
	 * @throws IOException If an error has occurred while closing the connections.
	 */
	public void closeConnections() throws IOException {
		
		// #1 Close the output stream
		if (this.objectOutputStream != null) {
			this.objectOutputStream.close();
		}

		// #2 close the input stream
		if (this.objectInputStream != null) {
			this.objectInputStream.close();
		}

		// #3 close the socket.
		if (this.socket != null) {
			this.socket.close();
		}
	}
	
	/**
	 * Send message to the server.
	 * This method should be called by a 3rd thread, which only handles the sending.
	 * This handler thread should only listen signals.
	 * 
	 * @param message      The message to be sent.
	 * @throws IOException Any exception thrown by the underlying OutputStream. 
	 */
	public void send(ChatMessage message) throws IOException {
		this.objectOutputStream.writeObject(message);
	}
}
