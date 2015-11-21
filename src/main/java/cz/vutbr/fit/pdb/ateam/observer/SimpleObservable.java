package cz.vutbr.fit.pdb.ateam.observer;

import java.util.Vector;

/**
 * @author Tomas Hanus
 * @author Tomas Mlynaric
 */
abstract public class SimpleObservable<T> {
	private Vector<T> observableList = new Vector<>();

	/**
	 * Subscribe listener to the list
	 *
	 * @param listener which will be notified
	 */
	public void subscribe(T listener) {
		this.observableList.add(listener);
	}

	/**
	 * Gets list of subscribed items
	 *
	 * @return
	 */
	public Vector<T> getObservableList() {
		return observableList;
	}

	/**
	 * Notifies all listeners based on interface method
	 */
	public void notifyObservers() {
	}

	/**
	 * Notifies all listeners and injects any object as parameter
	 * @param arg Object injected to the listener method in interface
	 */
	public void notifyObservers(Object arg) {
	}
}
