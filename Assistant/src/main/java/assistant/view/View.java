package assistant.view;

import javax.swing.JPanel;

import assistant.connection.Connection;

/**
 * Base class for a view that is going to be presented on the frame.
 * 
 * @author costi.dumitrescu
 */
public abstract class View extends JPanel {

	/**
	 * Default serial version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The connection, to be stared or stopped.
	 */
	protected Connection connection;
	
	/**
	 * Constructor.
	 * 
	 * @param connection The connection.
	 */
	public View(Connection connection) {
		// The connection.
		this.connection = connection; 
		// Initialize.
		this.initialize();
	}

	/**
	 * Initialize a view by adding all kind of elements that are necessarily for
	 * a particularly view.
	 */
	public abstract void initialize();
}
