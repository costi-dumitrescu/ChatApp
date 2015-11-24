package view;

import assistant.connection.ConnectionInfoPack;
import assistant.view.View;
import view.window.Window;
import view.window.WindowType;

/**
 * The {@link NotifiableView}
 * 
 * @author Costi.Dumitrescu
 */
public interface NotifiableView {

	/**
	 * Notifies the {@link View} to initialize a specific type of {@link Window} to
	 * be presented.
	 * 
	 * @param windowType The type of the window that is going to appear on the view.
	 */
	void setWindow(WindowType windowType);
	
	/**
	 * Sets the connection pack on the view, so at any time there will be access to it.
	 * 
	 * @param connectionInfoPack The information connection pack.
	 */
	void setConnectionInfoPack(ConnectionInfoPack connectionInfoPack);
	
	/**
	 * Returns the {@link ConnectionInfoPack} instance.
	 * 
	 * @return the {@link ConnectionInfoPack} instance.
	 */
	ConnectionInfoPack getConnectionInfoPack();
}
