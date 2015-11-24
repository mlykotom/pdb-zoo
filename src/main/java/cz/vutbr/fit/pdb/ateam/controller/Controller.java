package cz.vutbr.fit.pdb.ateam.controller;

import cz.vutbr.fit.pdb.ateam.adapter.DataManager;
import cz.vutbr.fit.pdb.ateam.exception.DataManagerException;
import cz.vutbr.fit.pdb.ateam.model.BaseModel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectTypeModel;
import cz.vutbr.fit.pdb.ateam.observer.IModelChangedStateListener;
import cz.vutbr.fit.pdb.ateam.observer.ModelChangedStateObservable;
import cz.vutbr.fit.pdb.ateam.observer.SpatialObjectsReloadObservable;
import cz.vutbr.fit.pdb.ateam.tasks.AsyncTask;
import cz.vutbr.fit.pdb.ateam.utils.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	 * Saves list of models to the database based on {@link DataManager#saveModel(BaseModel)} method.
	 * After saving, notifies listeners which listens for {@link IModelChangedStateListener}.
	 * Listeners are notified only when model is actualy saved! (if is changed)
	 *
	 * @param models list of models to saveModel
	 */
	public void saveModels(final List<? extends BaseModel> models) {
		new AsyncTask() {
			// models which were changed, these will be notified to listeners
			List<BaseModel> savedModels = new ArrayList<>();
			List<BaseModel> deletedModels = new ArrayList<>();

			@Override
			protected void onDone(boolean success) {
				for (BaseModel model : deletedModels) {
					ModelChangedStateObservable.getInstance().notifyObservers(model, IModelChangedStateListener.ModelState.DELETED);
				}

				for (BaseModel model : savedModels) {
					ModelChangedStateObservable.getInstance().notifyObservers(model, IModelChangedStateListener.ModelState.SAVED);
				}
			}

			@Override
			protected Boolean doInBackground(){
				for (BaseModel model : models) {
					try {
						if(model.isDeleted()){
							dataManager.deleteModel(model);
							deletedModels.add(model); // error could only happen when exception
							continue;
						}

						if (dataManager.saveModel(model)) {
							savedModels.add(model);
						}
					} catch (DataManagerException e) {
						e.printStackTrace();
						// TODO how to handle exceptions?
					}
				}

				// success if we saved at least one model
				return (deletedModels.size() + savedModels.size()) > 0;
			}
		}.start();
	}

	/**
	 * Save any changed model to the database.
	 *
	 * @param model spatial object must be changed (flag {@link BaseModel#isChanged()}), otherwise skipped
	 */
	public void saveModels(BaseModel model) {
		saveModels(Collections.singletonList(model));
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
					dataManager.reloadAllSpatialObjectTypes();
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

	/**
	 * Synchronouse method (available everywhere)
	 *
	 * @return cached spatial objects data from dataManager
	 */
	public ArrayList<SpatialObjectTypeModel> getSpatialObjectTypes() {
		return dataManager.getSpatialObjectTypes();
	}
}
