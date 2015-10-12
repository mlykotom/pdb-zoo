package controller;

import adapter.DataManager;

/**
 * Class for managing all view events.
 * <p>
 * Every frame or panel should have its own controller class, which
 * extends this class.
 * It's forbidden to instance this class from classes, which do not
 * extend this class.
 *
 * @author Jakub Tutko
 */
public class Controller {
	protected DataManager dataManager;

	/**
	 * Saves static DataManager instance into local variable.
	 */
	protected Controller() {
		dataManager = DataManager.getInstance();
	}
}
