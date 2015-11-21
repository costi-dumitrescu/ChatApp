package assistant.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.w3c.dom.Document;

/**
 * Each newly came client is handled by a {@link ListenerThread}.
 * 
 * @author costi.dumitrescu
 */
public abstract class ListenerThread extends Thread implements Handler {

	/**
	 * The socket where to listen/talk
	 * 
	 * A network socket is an end-point of an inter-process communication across
	 * a computer network. A socket address is the combination of an IP address
	 * and a port number. Internet sockets deliver incoming data packets to the
	 * appropriate application process or thread.
	 */
	protected Socket socket;
	
	/**
	 * To read from the socket.
	 */
	protected ObjectInputStream objectInputStream;
	
	/**
	 * To write on the socket.
	 */
	protected ObjectOutputStream objectOutputStream;
	
	/**
	 * The client associated with this listener.
	 */
	protected String username;
	
	/**
	 * Constructor.
	 * 
	 * @param socket The socket to read and right to.
	 */
	public ListenerThread(Socket socket) throws IOException {
		this.socket = socket;
		this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
		this.objectInputStream  = new ObjectInputStream(socket.getInputStream());
	}
	
	/**
	 * Sets the user-name.
	 * 
	 * @param username The user-name.
	 */
	public void setUsername(String username) {
		this.username = username;
	};
	
	/**
	 * Returns the user-name.
	 * 
	 * @return the user-name.
	 */
	public String getUsername() {
		return username;
	};
	
	/**
	 * Send a message to the client.
	 * 
	 * @param xmlMessage The message to be sent.
	 */
	public void writeMessage(Document xmlMessage) throws IOException {
		// Write the message to the stream
		this.objectOutputStream.writeObject(xmlMessage);
	}
	
	/**
	 * Shut down this current thread. Close everything.
	 * 
	 * @throws IOException If an error has occurred while closing the connections.
	 */
	public void closeConnections() throws IOException {
		// Try to close the connection.
		if (this.objectOutputStream != null) {
			this.objectOutputStream.close();
		}

		if (this.objectInputStream != null) {
			this.objectInputStream.close();
		}

		if (this.socket != null) {
			this.socket.close();
		}
	}
}
