package view.window;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import assistant.view.View;

/**
 * {@link Window} to be presented on a {@link View}.
 * 
 * @author Costi.Dumitrescu
 */
public abstract class Window extends JPanel {
	
	/**
	 * Logger for logging
	 */
	final static Logger logger = Logger.getLogger(Window.class);
	
	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */
	public Window() {
		this.initialize();
	}
	
	/**
	 * Initialize a {@link Window}.
	 */
	public abstract void initialize();
	
	/**
	 * Log a message in the {@link Window}.
	 * 
	 * @param message The message to be logged.
	 */
	public abstract void log(String message);

	/**
	 * Returns a lockable component to acquire locks on and to and to listen to.
	 * 
	 * @return a lockable Component to acquire locks on and to and to listen to.
	 */
	public abstract JComponent getLockableComponent();
}
