package assistant.message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import assistant.connection.Connection;
import assistant.connection.Handler;
import assistant.connection.ListenerThread;

/**
 * Message Handler. Factory design pattern implementation.
 * 
 * @author costi.dumitrescu
 */
public class MessageHandler {
	
	/**
	 * Connection instance.
	 */
	private static MessageHandler messageHandler; 
	
	/**
	 * Connection handler.
	 */
	private Handler connectionHandler;
	
	/**
	 * Document Builder. Defines the API to obtain DOM Document instances from
	 * an XML document.
	 */
	private DocumentBuilder documentBuilder;
	
	/**
	 * Private Constructor. Singleton purpose.
	 */
	private MessageHandler() {
	}
	
	/**
	 * Create/Return MessageHandler instance.
	 * 
	 * @return MessageHandler instance.
	 * 
	 * @throws ParserConfigurationException
	 *             If it fails creating a document builder.
	 */
	public synchronized static MessageHandler getInstance() {
		if (messageHandler == null) {
			messageHandler = new MessageHandler();
		}
		return messageHandler;
	}
	
	/**
	 * Creates a chat message instance according to the type.
	 * 
	 * @param messageType 	The type of the new chat message instance.
	 * @param username 		The user-name.
	 * @param message 		The actual message 
	 * @return 				The new message.
	 */
	public Document createMessage(String messageType, String username, String message) {
	
		// If null, create one.
		if (this.documentBuilder == null) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			try {
				this.documentBuilder = factory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				// Not much we can do!
				e.printStackTrace();
			}
		}
	
		// Holds the XML message.
		StringBuilder xml = new StringBuilder();
		
		// The XML message content. 
		xml.append("<?xml version='1.0'?>");
		xml.append("<ChatMessage>");
		xml.append("<MessageType>");
		xml.append(messageType);
		xml.append("</MessageType>");
		xml.append("<Username>");
		xml.append(username);
		xml.append("</Username>");
		xml.append("<Message>");
		xml.append(message);
		xml.append("</Message>");
		xml.append("</ChatMessage>");
		
		// A ByteArrayInputStream contains an internal buffer that contains
		// bytes that may be read from the stream. An internal counter keeps
		// track of the next byte to be supplied by the read method.
		ByteArrayInputStream input = null;
		try {
			input = new ByteArrayInputStream(xml.toString().getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// Not much we can do!
			e.printStackTrace();
		}
	
		// The Document interface represents the entire HTML or XML document.
		Document document = null;
		try {
			document = this.documentBuilder.parse(input);
		} catch (SAXException e) {
			// Not much we can do!
			e.printStackTrace();
		} catch (IOException e) {
			// Not much we can do!
			e.printStackTrace();
		}
		
		return document;
	}
	
	/**
	 * Parses the document and notifies the connection about updates.
	 * 
	 * @param listenerThread The current listener thread.
	 * @param xmlMessage     The XML message to be parsed.
	 */
	public void handleMessage(ListenerThread listenerThread, Document xmlMessage) {
			
		// For safety.
		if(xmlMessage != null) {
			
			// The message type node.
			Node messageType = null;
			
			// The name of the user.
			Node username = null;
			
			// The message.
			Node message = null;
			
			// Root element of the document.
			Element root = xmlMessage.getDocumentElement();
			
			// All MessageType tags within root element. 
			NodeList messageTypeList = root.getElementsByTagName("MessageType");
			
			// Extra safety,
			if(messageTypeList.getLength() > 0) {
				messageType = messageTypeList.item(0);
			}

			// All Username tags within root element. 
			NodeList usernameList = root.getElementsByTagName("Username");
			
			// Extra safety,
			if(usernameList.getLength() > 0) {
				username = usernameList.item(0);
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
				// ServerConnection - Should broadcast the message to all others, to say 'user : logged in'
				//					- Should send a WHOISIN message to update the list of users for each client.
				// ClientConnection - Should log the message on the screen/or somehow the current should be announced that someone has logged in.
				this.connectionHandler.handleLogin(listenerThread, username.getTextContent(), message.getTextContent());
				break;

			// "User asked who is in." 
			case MessageType.WHOISIN:
				// ServerConnection - Should loop through the list of clients and send them all to the current asking client.
				// ClientConnection - First clear the users table and recreate it.
				this.connectionHandler.handleWhoisin(listenerThread, username.getTextContent(), message.getTextContent());
				break;

			// Message.
			case MessageType.MESSAGE:
				// ServerConnection - Broadcast the message to all clients.
				// ClientConnection - Just print/log it.
				this.connectionHandler.handleMessage(listenerThread, username.getTextContent(), message.getTextContent());
				break;

			// "User logged out."
			case MessageType.LOGOUT:
				// ServerConnection - Should broadcast the message to all others, to say 'user : logged out'
				//					- Should send a WHOISIN message to update the list of users for each client.
				// ClientConnection - Change the MainView with the LoginView.
				this.connectionHandler.handleLogout(listenerThread, username.getTextContent(), message.getTextContent());
				break;

			default:
				break;
			}
		}
	}

	/**
	 * Sets the connection handler.
	 * 
	 * @param connectionHandler The connection handler.
	 */
	public void setConnectionHandler(Connection connectionHandler) {
		this.connectionHandler = connectionHandler;
	}
}
