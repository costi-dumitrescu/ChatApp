package server.connection;

import java.io.IOException;
import java.util.ArrayList;

import assistant.handler.HandlerThread;
import assistant.message.ChatMessage;

/**
 * {@link ServerRoom} DAO. This singleton holds all clients connected to the server, and
 * has specific methods to handle them.
 * 
 * @author Costi.Dumitrescu
 */
public class ServerRoom {

	/**
	 * {@link ServerRoom} Instance. Singleton purpose.
	 */
	private static ServerRoom INSTANCE;

	/**
	 * The list of all the Clients that are connected to this server.
	 */
	private ArrayList<ServerHandlerThread> clients;

	/**
	 * Private constructor. Singleton purpose.
	 */
	private ServerRoom() {
		// Initialize the empty clients list.
		this.clients = new ArrayList<>();
	}

	/**
	 * Returns the single reference for the {@link ServerRoom} instance.
	 * Singleton purpose.
	 * 
	 * @return The single reference for the {@link ServerRoom} instance.
	 */
	public static synchronized ServerRoom getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ServerRoom();
		}
		return INSTANCE;
	}

	/**
	 * Add a new client in the room.
	 * 
	 * @param client The new client to be added in the room.
	 */
	public void addClient(ServerHandlerThread client) {
		
		// TODO - specific behavior before adding the new client.
		//		- when a user in added in the room, all others have to be informed about this.
		//		- wait + notify here.
		
		this.clients.add(client);
	}
	
	/**
	 * Remove a client from the room.
	 * 
	 * @param client 	   The client to be removed from the room.
	 * @throws IOException If an error has occurred while closing the connections.
	 */
	public void removeClient(ServerHandlerThread client) throws IOException {
		
		// TODO - specific behavior before removing the client.
		//		- when a user in removed from the room, all others have to be informed about this.
		//		- wait + notify here.
		// 		- closeConnections() should be called by the thread him self, not by someone else. 
		
		client.closeConnections();
		this.clients.remove(client);
	}
	
	/**
	 * Shut all clients.The server is going down.The server thread has to wait
	 * for all client threads to close their connections.
	 * 
	 * @throws IOException If an I/O error has occurred while closing the connections.
	 */
	public void removeAllClients() throws IOException {
		
		// TODO - specific behavior before removing the client.
		//		- wait + notify here.
		// 		- join() here
		// 		- before closing the connection, a handler thread has to stop running.
		
		for (ServerHandlerThread client : this.clients) {
			this.removeClient(client);
		}
	}
	
	/**
	 * Broadcast a message to all clients in the room.
	 * 
	 * @param message 	   The message to be broadcasted to all clients.
	 * @throws IOException Any exception thrown by the underlying OutputStream. 
	 */
	public void broadcast(ChatMessage message) throws IOException {
		
		// TODO - specific behavior before broadcasting the message.
		//		- wait + notify here.
		
		// Loop through the list of clients and send the message to each of
		// them.
		for (HandlerThread client : this.clients) {
			client.writeMessage(message);
		}
	}
	
	/**
	 * Returns a {@link StringBuilder} with all users in the room separated by comma. 
	 * 
	 * @return a {@link StringBuilder} with all users in the room separated by comma.
	 */
	public StringBuilder listAllClients() {
		// Holds all clients.
		StringBuilder clients = new StringBuilder();
		for (ServerHandlerThread client : this.clients) {
			clients.append(client.getUser());
			clients.append(",");
		}
		return clients;
	}
}
