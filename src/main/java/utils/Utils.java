package utils;

import adapter.DataManager;

/**
 * Class includes helpful static methods used in the whole application.
 *
 * @author Jakub Tutko
 */
public class Utils {

	/**
	 * Disconnects database and closes application.
	 */
	public static void closeApplication() {
		DataManager.getInstance().disconnectDatabase();
		Logger.createLog(Logger.DEBUG_LOG, "Closing application with System.exit(0).");
		System.exit(0);
	}
}
