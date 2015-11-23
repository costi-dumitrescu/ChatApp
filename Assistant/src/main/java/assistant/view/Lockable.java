package assistant.view;

/**
 * {@link Lockable}.
 * 
 * @author Costi.Dumitrescu
 */
public interface Lockable {

	/**
	 * Returns a lockable object to acquire locks on and to and to listen to.
	 * 
	 * @return a lockable object to acquire locks on and to and to listen to.
	 */
	Object getLockableObject();
}
