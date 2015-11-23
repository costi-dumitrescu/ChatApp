package view.window;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import assistant.message.Messages;

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
	 * Constructor.
	 */
	public ChatWindow() {
	}

	/**
	 * @see assistant.view.Viewable.createView()
	 */
	public void initialize() {// TODO - LOGOUT BUTTON.
		
		this.setLayout(new GridBagLayout());
		this.setBorder(BorderFactory.createTitledBorder("Chat Application"));
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Chat
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

		// Users.
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
		
		// South Panel.
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbcSouth = new GridBagConstraints();

		this.inputTextArea = new JTextArea();
		this.inputTextArea.setLineWrap(true);
		JScrollPane inputScrollPane = new JScrollPane(this.inputTextArea);
		inputScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		gbcSouth.gridx = 0;
		gbcSouth.gridy = 0;
		gbcSouth.fill = GridBagConstraints.BOTH;
		gbcSouth.weightx = 1.0;
		gbcSouth.weighty = 1.0;
		gbcSouth.insets = new Insets(2, 2, 2, 2);
		southPanel.add(inputScrollPane, gbcSouth);

		this.sendButton = new JButton("Send");
		gbcSouth.gridx++;
		gbcSouth.weightx = 0.0;
		southPanel.add(this.sendButton, gbcSouth);

		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth++;
		gbc.insets = new Insets(2, 2, 2, 2);
		this.add(southPanel, gbc);}
	
	/**
	 * Returns the chat text area.
	 * 
	 * @return the chat text area.
	 */
	public JTextArea getChatTextArea() {
		return chatTextArea;
	}
	
	/**
	 * Returns the users table.
	 * 
	 * @return the users table.
	 */
	public JTable getUsersTable() {
		return usersTable;
	}
	
	
	/**
	 * Returns the input text area.
	 * 
	 * @return the input text area.
	 */
	public JTextArea getInputTextArea() {
		return inputTextArea;
	}
	
	/**
	 * Returns the send button.
	 * 
	 * @return the send button.
	 */
	public JButton getSendButton() {
		return sendButton;
	}
	
	/**
	 * @see view.window.Window.log(String)
	 */
	@Override
	public void log(String message) {
		
		// #1 log4j
		logger.warn(message);
		
		// First check if this is a NEW_CLIENTS type message.
		if (message.contains(Messages.NEW_CLIENTS)) {
			this.removeAllUsers();
			message = message.substring(0, message.indexOf(Messages.NEW_CLIENTS));
			String[] users = message.split(Messages.COMMA);
			for (String user : users) {
				this.appendUser(user);
			}
		}
		// Otherwise just a normal message (or even exception)
		else {
			this.chatTextArea.append(message + "\n");
			// The caret at the end.
			this.chatTextArea.setCaretPosition(this.chatTextArea.getDocument().getLength());
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
	 * Clear the list of users.
	 */
	private void removeAllUsers() {
		DefaultTableModel tableModel = (DefaultTableModel) this.usersTable.getModel();
		if (tableModel.getRowCount() > 0) {
		    for (int i = tableModel.getRowCount() - 1; i > -1; i--) {
		    	tableModel.removeRow(i);
		    }
		}
	}
}