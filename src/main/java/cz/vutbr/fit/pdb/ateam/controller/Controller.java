package cz.vutbr.fit.pdb.ateam.controller;

import cz.vutbr.fit.pdb.ateam.adapter.DataManager;
import cz.vutbr.fit.pdb.ateam.exception.DataManagerException;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.observer.SpatialObjectsReloadObservable;
import cz.vutbr.fit.pdb.ateam.tasks.AsyncTask;
import cz.vutbr.fit.pdb.ateam.utils.Logger;

import java.util.ArrayList;

/**
 * Class for managing all view events.
 * <p/>
 * Every frame or panel should have its own cz.vutbr.fit.pdb.ateam.cz.vutbr.fit.pdb.ateam.controller class, which
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

	/**
	 * Reloads all data when application starts
	 * TODO one async task, many queries
	 */
	public void reloadAllData() {
		reloadSpatialObjects();
	}

	/**
	 * Asynchronously reloads data and store as cached data in dataManager.
	 * Data are accessible through {@link #getSpatialObjects()} method
	 */
	public void reloadSpatialObjects() {
		AsyncTask task = new AsyncTask() {
			@Override
			protected void onDone(boolean success) {
				SpatialObjectsReloadObservable.getInstance().notifyObservers();
			}

			@Override
			protected Boolean doInBackground() throws Exception {
				try {
					dataManager.reloadAllSpatialObjects();
					return true;
				} catch (DataManagerException e) {
					Logger.createLog(Logger.ERROR_LOG, e.getMessage());
				}
				return false;
			}
		};

		task.start();
	}

	/**
	 * Synchronouse method (available everywhere)
	 *
	 * @return cached spatial objects data from dataManager
	 */
	public ArrayList<SpatialObjectModel> getSpatialObjects() {
		return dataManager.getSpatialObjects();
	}
}
