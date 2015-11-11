package cz.vutbr.fit.pdb.ateam.controller;

import cz.vutbr.fit.pdb.ateam.adapter.DAL;
import cz.vutbr.fit.pdb.ateam.adapter.DataManager;

/**
 * Class for managing all view events.
 * <p>
 * Every frame or panel should have its own cz.vutbr.fit.pdb.ateam.cz.vutbr.fit.pdb.ateam.controller class, which
 * extends this class.
 * It's forbidden to instance this class from classes, which do not
 * extend this class.
 *
 * @author Jakub Tutko
 */
public class Controller {
	protected DataManager dataManager;
	protected DAL dal;

	/**
	 * Saves static DataManager instance into local variable.
	 */
	protected Controller() {
		dataManager = DataManager.getInstance();
		dal = DAL.getInstance();
	}
}
