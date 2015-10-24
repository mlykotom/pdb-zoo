package cz.vutbr.fit.pdb.ateam.controller;

import cz.vutbr.fit.pdb.ateam.adapter.DataManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for managing all view events.
 * <p>
 * Every frame or panel should have its own cz.vutbr.fit.pdb.ateam.controller class, which
 * extends this class.
 * It's forbidden to instance this class from classes, which do not
 * extend this class.
 *
 * @author Jakub Tutko
 */
public class Controller {
	protected DataManager dataManager;
	private List<ObjectSelectionChangedListener> objectSelectionChangedListeners = new ArrayList<ObjectSelectionChangedListener>();

	/**
	 * Saves static DataManager instance into local variable.
	 */
	protected Controller() {
		dataManager = DataManager.getInstance();
	}



	public interface ObjectSelectionChangedListener{
		void notifyObjectSelectionChanged();
	}

	public void notifyAllObjectSelectionChangedListeners(){
		for (ObjectSelectionChangedListener listener :  this.objectSelectionChangedListeners){
			listener.notifyObjectSelectionChanged();
		}
	}
}
