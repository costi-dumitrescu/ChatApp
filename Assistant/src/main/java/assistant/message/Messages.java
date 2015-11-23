package assistant.message;

/**
 * Messages.
 *
 * @author Costi.Dumitrescu
 */
public interface Messages {

	// Sent by the client handler thread when a user has just logged in.
	String USER_IN = "User just logged in.";

	// To be appended at the normal String, when a clients received the list of
	// clients.
	String NEW_CLIENTS = "NEW_CLIENTS";
	
	// Comma separation for each client packed in a message.
	String COMMA = ",";
	
	// Exception in client thread
	String EXCEPTION_IN_CLIENT_THREAD = "Exception in client thread";
	
	// Separator
	String SEPARATOR = " : ";
}
