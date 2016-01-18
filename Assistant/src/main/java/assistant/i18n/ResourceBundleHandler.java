package assistant.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Creates a {@link ResourceBundleHandler} according to a {@link Locale}.
 * 
 * @author costi.dumitrescu
 */
public class ResourceBundleHandler {

	/**
	 * The base name for the i18n property files.
	 */
	private String baseName = "Labels";

	/**
	 * The current {@link Locale} used by the {@link ResourceBundleHandler}. English by default.
	 */
	private Locale currentLocale = Locale.ENGLISH;

	/**
	 * {@link ResourceBundleHandler} Instance. Singleton purpose.
	 */
	private static ResourceBundleHandler INSTANCE;

	/**
	 * Private constructor. Singleton Purpose.
	 */
	private ResourceBundleHandler() {
	}

	/**
	 * Returns the single reference for the {@link ResourceBundleHandler}
	 * instance. Singleton purpose.
	 * 
	 * @return The single reference for the {@link ResourceBundleHandler}
	 *         instance.
	 */
	public synchronized static ResourceBundleHandler getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ResourceBundleHandler();
		}
		return INSTANCE;
	}

	/**
	 * Update the {@link Locale}, and recreate the {@link ResourceBundleHandler}.
	 * 
	 * @param newLocale The new {@link Locale}.
	 */
	public boolean updateLocale(Locale newLocale) {
		// The status
		boolean status = false;
		// Only if we have a different {@link Locale}.
		if (newLocale != null && newLocale != currentLocale) {
			this.currentLocale = newLocale;
			status = true;
		}
		return status;
	}

	/**
	 * Get the {@link ResourceBundle} calculated for the current {@link Locale}. The
	 * {@link ResourceBundle} instance automatically gets cached.
	 * 
	 * @return The {@link ResourceBundle} calculated for the current {@link Locale}.
	 */
	public ResourceBundle getResourceBundle() {
		return ResourceBundle.getBundle(this.baseName, this.currentLocale);
	}
}
