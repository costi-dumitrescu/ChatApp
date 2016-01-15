package view.window;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.ParserConfigurationException;

import assistant.connection.Connection;
import assistant.message.ChatMessage;
import assistant.message.MessageHandler;
import assistant.message.MessageType;
import assistant.message.Messages;
import assistant.message.rooms.arrivals.LoginMessagesRoom;
import assistant.message.rooms.arrivals.LogoutMessagesRoom;
import assistant.message.rooms.arrivals.NormalMessagesRoom;
import assistant.message.rooms.arrivals.WhoisinMessagesRoom;
import assistant.message.rooms.departures.OutgoingMessagesRoom;
import view.NotifiableView;

/**
 * The main view.
 * 
 * @author costi.dumitrescu
 */
public class ChatWindow extends Window {

	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The chat text area.
	 */
	private JTextArea chatTextArea;
	
	/**
	 * This table presents all users in the room.
	 */
	private JTable usersTable;
	
	/**
	 * The input text area. Here is where the user is typing the messages.
	 */
	private JTextArea inputTextArea;
	
	/**
	 * The send button. After a message is written, the user has to press 'send'
	 * the send the message.
	 */
	private JButton sendButton;
	
	/**
	 * <code>true</code> as long as the connection is opened.
	 * 
	 * TODO : on stop/logout this should go to false.
	 */
	private boolean isConnectionOpened;

	/**
	 * Constructor.
	 * 
	 * @param connection 	 The {@link Connection}
	 * @param notifiableView The {@link NotifiableView} 
	 */
	public ChatWindow(Connection connection, NotifiableView notifiableView) {
		super(connection, notifiableView);
		
		// GO
		this.isConnectionOpened = true;
		
		// login messages listener thread.
		this.createListenerThreadForIncomingLoginMessages();
		
		// messages listener thread.
		this.createListenerThreadForIncomingMessages();
		
		// users listener thread.
		this.createListenerThreadForIncomingUsers();
		
		// logout messages listener thread.
		this.createListenerThreadForIncomingLogoutMessages();
	}

	/**
	 * TODO - LOGOUT BUTTON -> ask the notifiable view to change its window.
	 * 
	 * @see assistant.view.Viewable.createView()
	 */
	public void initialize() {
		
		// {@link GridBagLayout}
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		// CHAT TEXT AREA.
		this.chatTextArea = new JTextArea();
		this.chatTextArea.setLineWrap(true);
		this.chatTextArea.setEditable(false);
		JScrollPane chatScrollPane = new JScrollPane(this.chatTextArea);
		chatScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.insets = new Insets(2, 2, 2, 2);
		this.add(chatScrollPane, gbc);

		// USERS TABLE.
		DefaultTableModel usersModel = new DefaultTableModel() {
			/**
			 * Serial version.
			 */
			private static final long serialVersionUID = 1L;

			/**
			 * @see javax.swing.table.DefaultTableModel.isCellEditable(int, int)
			 */
			@Override
			public boolean isCellEditable(int row, int column) {
				// all cells false
				return false;
			}
		};
		usersModel.addColumn("Users");
		this.usersTable = new JTable(usersModel);
		this.usersTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		// Align the text in the middle of column.
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		for (int i = 0; i < this.usersTable.getColumnCount(); i++) {
			usersTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}
		JScrollPane usersScrollPane = new JScrollPane(this.usersTable);
		gbc.gridx++;
		this.add(usersScrollPane, gbc);
		
		// SOUTH PANEL.
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbcSouth = new GridBagConstraints();
		
		// INPUT TEXT AREA.
		this.inputTextArea = new JTextArea();
		this.inputTextArea.setLineWrap(true);
		this.inputTextArea.addKeyListener(new KeyListener(){
			/**
			 * @see java.awt.event.KeyListener.keyTyped(KeyEvent)
			 */
			public void keyTyped(KeyEvent e) {
				// Do nothing!
			}
			/**
			 * @see java.awt.event.KeyListener.keyPressed(KeyEvent)
			 */
			public void keyPressed(KeyEvent e) {
				// Do nothing!
			}
			/**
			 * @see java.awt.event.KeyListener.keyReleased(KeyEvent)
			 */
			public void keyReleased(KeyEvent e) {
				// If ENTER has been pressed then send the message.
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					// DO
					ChatWindow.this.doSendNormalMessage();
				}
			}
		});
		JScrollPane inputScrollPane = new JScrollPane(this.inputTextArea);
		inputScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		gbcSouth.gridx = 0;
		gbcSouth.gridy = 0;
		gbcSouth.fill = GridBagConstraints.BOTH;
		gbcSouth.weightx = 1.0;
		gbcSouth.weighty = 1.0;
		gbcSouth.insets = new Insets(2, 2, 2, 2);
		southPanel.add(inputScrollPane, gbcSouth);

		// SEND BUTTON
		this.sendButton = new JButton("Send");
		this.sendButton.addActionListener(new ActionListener() {
			/**
			 * @see java.awt.event.ActionListener.actionPerformed(ActionEvent)
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				// DO
				ChatWindow.this.doSendNormalMessage();
			}
		});
		gbcSouth.gridx++;
		gbcSouth.weightx = 0.0;
		southPanel.add(this.sendButton, gbcSouth);
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth++;
		gbc.insets = new Insets(2, 2, 2, 2);
		this.add(southPanel, gbc);
	}
	
	/**
	 * This thread listens on the {@link LoginMessagesRoom} list and update the
	 * chat text area.
	 */
	private void createListenerThreadForIncomingLoginMessages() {
		// create a new thread to handle this.
		new Thread("Listener-Thread-For-Login-Messages") {
			/**
			 * @see java.lang.Runnable.run()
			 */
			@Override
			public void run() {
				// forever.
				while (ChatWindow.this.isConnectionOpened) {
					// Synchronize on the list of messages.
					synchronized (LoginMessagesRoom.getInstance()) {
						try {
							LoginMessagesRoom.getInstance().wait();
						} catch (InterruptedException e) {
							// Not much we can do. Just continue.
							continue;
						}
						// This thread could be released by mistake, so we have to check the size.
						if(LoginMessagesRoom.getInstance().getMessages().size() > 0) {
							// Loop through all messages (even if at the list will have only one message at any time)
							for (String message : LoginMessagesRoom.getInstance().getMessages()) {
								// Append message in the chat text area.
								ChatWindow.this.chatTextArea.append(message + "\n");
								// Put the caret at the end.
								ChatWindow.this.chatTextArea.setCaretPosition(ChatWindow.this.chatTextArea.getDocument().getLength());
							}
							// Remove all messages.
							LoginMessagesRoom.getInstance().getMessages().clear();
						}
					}
				}
			};
		}.start();
	}
	
	/**
	 * This thread listens on the {@link NormalMessagesRoom} list and update the
	 * chat text area.
	 */
	private void createListenerThreadForIncomingMessages() {
		// create a new thread to handle this.
		new Thread("Listener-Thread-For-Messages") {
			/**
			 * @see java.lang.Runnable.run()
			 */
			@Override
			public void run() {
				// forever.
				while (ChatWindow.this.isConnectionOpened) {
					// Synchronize on the list of messages.
					synchronized (NormalMessagesRoom.getInstance()) {
						try {
							NormalMessagesRoom.getInstance().wait();
						} catch (InterruptedException e) {
							// Not much we can do. Just continue.
							continue;
						}
						// This thread could be released by mistake, so we have to check the size.
						if(NormalMessagesRoom.getInstance().getMessages().size() > 0) {
							// Loop through all messages (even if at the list will have only one message at any time)
							for (String message : NormalMessagesRoom.getInstance().getMessages()) {
								// Append message in the chat text area.
								ChatWindow.this.chatTextArea.append(message + "\n");
								// Put the caret at the end.
								ChatWindow.this.chatTextArea.setCaretPosition(ChatWindow.this.chatTextArea.getDocument().getLength());
							}
							// Remove all messages.
							NormalMessagesRoom.getInstance().getMessages().clear();
						}
					}
				}
			};
		}.start();
	}
	
	/**
	 * This thread listens on the {@link WhoisinMessagesRoom} list and update the
	 * users table.
	 */
	private void createListenerThreadForIncomingUsers() {
		// create a new thread to handle this.
		new Thread("Listener-Thread-For-Users") {
			/**
			 * @see java.lang.Runnable.run()
			 */
			@Override
			public void run() {
				// forever.
				while (ChatWindow.this.isConnectionOpened) {
					// Synchronize on the list of messages.
					synchronized (WhoisinMessagesRoom.getInstance()) {
						try {
							WhoisinMessagesRoom.getInstance().wait();
						} catch (InterruptedException e) {
							// Not much we can do. Just continue.
							continue;
						}
						// This thread could be released by mistake, so we have to check the size.
						if(WhoisinMessagesRoom.getInstance().getMessages().size() > 0) {
							// Loop through all messages (even if at the list will have only one message at any time)
							// A message represents the list of users, separated by comma.
							for (String message : WhoisinMessagesRoom.getInstance().getMessages()) {
								ChatWindow.this.removeAllUsers();
								String[] users = message.split(Messages.COMMA);
								for (String user : users) {
									ChatWindow.this.appendUser(user);
								}
							}
							// Remove all messages.
							WhoisinMessagesRoom.getInstance().getMessages().clear();
						}
					}
				}
			};
		}.start();
	}
	
	/**
	 * Remove all users.
	 */
	private void removeAllUsers() {
		DefaultTableModel tableModel = (DefaultTableModel) this.usersTable.getModel();
		if (tableModel.getRowCount() > 0) {
		    for (int i = tableModel.getRowCount() - 1; i > -1; i--) {
		    	tableModel.removeRow(i);
		    }
		}
	}
	
	/**
	 * Add another user-name in the table of users.
	 * 
	 * @param username The user-name to be added.
	 */
	private void appendUser(String username) {
		// Add this username only if it is not null or empty.
		if (username != null && !("".equals(username))) {
			// Get the model.
			DefaultTableModel usersTableModel = (DefaultTableModel) this.usersTable.getModel();
			// Append the user-name.
			usersTableModel.addRow(new Object[] { username });
		}
	}
	
	/**
	 * This thread listens on the {@link LogoutMessagesRoom} list and update the
	 * chat text area.
	 */
	private void createListenerThreadForIncomingLogoutMessages() {
		// create a new thread to handle this.
		new Thread() {
			/**
			 * @see java.lang.Runnable.run()
			 */
			@Override
			public void run() {
				// forever.
				while (ChatWindow.this.isConnectionOpened) {
					// Synchronize on the list of messages.
					synchronized (LogoutMessagesRoom.getInstance()) {
						try {
							LogoutMessagesRoom.getInstance().wait();
						} catch (InterruptedException e) {
							// Not much we can do. Just continue.
							continue;
						}
						// This thread could be released by mistake, so we have to check the size.
						if(LogoutMessagesRoom.getInstance().getMessages().size() > 0) {
							// Loop through all messages (even if at the list will have only one message at any time)
							for (String message : LogoutMessagesRoom.getInstance().getMessages()) {
								// Append message in the chat text area.
								ChatWindow.this.chatTextArea.append(message + "\n");
								// Put the caret at the end.
								ChatWindow.this.chatTextArea.setCaretPosition(ChatWindow.this.chatTextArea.getDocument().getLength());
							}
							// Remove all messages.
							LogoutMessagesRoom.getInstance().getMessages().clear();
						}
					}
				}
			};
		}.start();
	}
	
	/**
	 * To be done when the 'Send' button was pressed. This method is also called
	 * when ENTER is pressed inside input text area. If there is no text in the
	 * input text area, nothing is sent to the server. After sending the
	 * message, the text area is cleared, so the next time the user types text
	 * to be sent, only that text gets sent.
	 * 
	 * TODO : If this method gets called by the button, then after the click on
	 * the button, the input text-area has to be focused again.
	 */
	private void doSendNormalMessage() {
		// The message to be sent, a little bit modified.
		final String message = this.inputTextArea.getText().replace("\n", " ").replace("\r", " ").trim();
		// Check to see if the input text area is empty
		if (!("".equals(message))) {
			// create a new thread to handle this.
			new Thread("Do-Send-Normal-Message-Thread") {
				/**
				 * @see java.lang.Runnable.run()
				 */
				@Override
				public void run() {
					try {
						String user = ChatWindow.this.notifiableView.getConnectionInfoPack().getUser();
						// Create the {@link ChatMessage}.
						ChatMessage chatMessage = MessageHandler.getInstance().createMessage(MessageType.MESSAGE, user, message);
						// The chosen lockable object
						synchronized (OutgoingMessagesRoom.getInstance()) {
							// Add the message
							OutgoingMessagesRoom.getInstance().getMessages().add(chatMessage);
							// Notify the waiter
							OutgoingMessagesRoom.getInstance().notify();
						}
					} catch (ParserConfigurationException e) {
						// Not much we can do.
					}
				}
			}.start();
			// After the message is sent, clear the input text area.
			this.inputTextArea.setText(null);
		}
	}
}