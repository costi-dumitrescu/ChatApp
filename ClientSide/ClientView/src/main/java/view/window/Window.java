package view.window;

import javax.swing.JPanel;

import assistant.connection.Connection;
import assistant.view.View;
import view.NotifiableView;

/**
 * {@link Window} to be presented on a {@link View}.
 * 
 * @author Costi.Dumitrescu
 */
public abstract class Window extends JPanel {
	
	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The {@link NotifiableView}
	 */
	protected NotifiableView notifiable;
	
	/**
	 * The {@link Connection} 
	 */
	protected Connection connection;
	
	/**
	 * Constructor.
	 * 
	 * @param connection The {@link Connection}
	 * @param notifiable The {@link NotifiableView}
	 */
	public Window(Connection connection, NotifiableView notifiable) {
		this.connection = connection;
		this.notifiable = notifiable;
		this.initialize();
	}
	
	/**
	 * Initialize a {@link Window}.
	 */
	public abstract void initialize();
}
