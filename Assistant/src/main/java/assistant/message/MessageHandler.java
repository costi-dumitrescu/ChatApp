package assistant.message;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import assistant.handler.Handler;

/**
 * {@link MessageHandler}. Factory design pattern implementation.
 * 
 * @author costi.dumitrescu
 */
public class MessageHandler {
	
	/**
	 * {@link MessageHandler} Instance. Singleton purpose.
	 */
	private static MessageHandler messageHandler; 
	
	/**
	 * {@link DocumentBuilder}. Defines the API to obtain DOM {@link Document} instances from
	 * an XML document.
	 */
	private DocumentBuilder documentBuilder;
	
	/**
	 * Private Constructor. Singleton purpose.
	 */
	private MessageHandler() {
	}
	
	/**
	 * Returns the single reference for the {@link MessageHandler} instance.
	 * Singleton purpose.
	 * 
	 * @return The single reference for the {@link MessageHandler} instance.
	 */
	public synchronized static MessageHandler getInstance() {
		if (messageHandler == null) {
			messageHandler = new MessageHandler();
		}
		return messageHandler;
	}
	
	/**
	 * Create a {@link ChatMessage} wrapper for a {@link Document}.
	 *
	 * {@link Document} example : 
	 *  
	 *	<ChatMessage>
	 *		<MessageType>
	 *			messageType
	 *		</MessageType>
	 *		<User>
	 *			user
	 *		</User>
	 *		<Message>
	 *			message
	 *		</Message>
	 *	</ChatMessage>
	 * 
	 * @param messageType 					The type of the new {@link ChatMessage}.
	 * @param user	 						The user.
	 * @param message 						The actual message.
	 * 
	 * @return 								The new {@link ChatMessage}.
	 * 
	 * @throws ParserConfigurationException If a DocumentBuilder cannot be created which satisfies the configuration requested.
	 */
	public ChatMessage createMessage(String messageType, String user, String message) throws ParserConfigurationException {
	
		// If null, create one.
		if (this.documentBuilder == null) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			this.documentBuilder = factory.newDocumentBuilder();
		}
	
		// The {@link Document}
		Document document = this.documentBuilder.newDocument();
		
		// The 'ChatMessage' Root {@link Element}
		Element chatMessageRootElement = document.createElement("ChatMessage");
		document.appendChild(chatMessageRootElement);

		// The 'MessageType' {@link Element}
		Element messageTypeElement = document.createElement("MessageType");
		messageTypeElement.appendChild(document.createTextNode(messageType));
		chatMessageRootElement.appendChild(messageTypeElement);

		// The 'User' {@link Element}
		Element userElement = document.createElement("User");
		userElement.appendChild(document.createTextNode(user));
		chatMessageRootElement.appendChild(userElement);

		// The 'Message' {@link Element} 
		Element messageElement = document.createElement("Message");
		messageElement.appendChild(document.createTextNode(message));
		chatMessageRootElement.appendChild(messageElement);

		// The actual {@link ChatMessage}
		ChatMessage chatMessage = new ChatMessage(document);
		
		return chatMessage;
	}
	
	/**
	 * Parses the {@link ChatMessage} and notifies the handler.
	 * 
	 * @param handler 	 					The handler thread.
	 * @param chatMessage 					The XML wrapper {@link ChatMessage} to be parsed.
	 *
	 * @throws IOException 					Any exception thrown by the underlying OutputStream. 
	 * @throws ParserConfigurationException If a DocumentBuilder cannot be created which satisfies the configuration requested.
	 * @throws DOMException 				DOMSTRING_SIZE_ERR: Raised when it would return more characters than fit in a DOMString 
	 * 										variable on the implementation platform.
	 */
	public void handleMessage(Handler handler, ChatMessage chatMessage) throws DOMException, IOException, ParserConfigurationException {
			
		// For safety.
		if(handler != null && chatMessage != null) {
			
			// {@link Document} XML message.
			Document xmlMessage = chatMessage.getMessage();
			
			// {@link Node} type.
			Node messageType = null;
			
			// {@link Node} user.
			Node user = null;
			
			// {@link Node} message.
			Node message = null;
			
			// {@link Element} Root element of the document.
			Element root = xmlMessage.getDocumentElement();
			
			// {@link NodeList} All MessageType tags within root element. 
			NodeList messageTypeList = root.getElementsByTagName("MessageType");
			
			// Extra safety,
			if(messageTypeList.getLength() > 0) {
				messageType = messageTypeList.item(0);
			}

			// All user tags within root element. 
			NodeList userList = root.getElementsByTagName("User");
			
			// Extra safety,
			if(userList.getLength() > 0) {
				user = userList.item(0);
			}

			// All Message tags within root element. 
			NodeList messageList = root.getElementsByTagName("Message");

			// Extra safety,
			if(messageList.getLength() > 0) {
				message = messageList.item(0);
			}
			
			// Different behaviors for each message type.
			switch (messageType.getTextContent()) {
			
				// "User logged in."
				case MessageType.LOGIN:
					// ServerConnection - Broadcast the message to all others, to say 'user : logged in'
					//					- Send a WHOISIN message to all clients, to update the list of users with this new one.
					// ClientConnection - Should log the message on the screen/or somehow the current should be announced that someone has logged in.
					handler.handleLogin(user.getTextContent(), message.getTextContent());
					break;
	
				// "User asked who is in." 
				case MessageType.WHOISIN:
					// ServerConnection - Should send the list of clients to the current asking client.
					// ClientConnection - First clear the users table and recreate it according with the new one.
					handler.handleWhoIsIn(user.getTextContent(), message.getTextContent());
					break;
	
				// 'User sent a message for the room.'
				case MessageType.MESSAGE:
					// ServerConnection - Broadcast the message to all clients.
					// ClientConnection - Just print/log it.
					handler.handleMessage(user.getTextContent(), message.getTextContent());
					break;
	
				// "User logged out."
				case MessageType.LOGOUT:
					// ServerConnection - Should broadcast the message to all others, to say 'user : logged out'
					//					- Should send a WHOISIN message to update the list of users for each client.
					// ClientConnection - Change the MainView with the LoginView.
					handler.handleLogout(user.getTextContent(), message.getTextContent());
					break;
	
				default:
					break;
			}
		}
	}
}
