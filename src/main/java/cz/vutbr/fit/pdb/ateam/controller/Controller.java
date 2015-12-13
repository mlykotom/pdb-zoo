package cz.vutbr.fit.pdb.ateam.controller;

import cz.vutbr.fit.pdb.ateam.adapter.DataManager;
import cz.vutbr.fit.pdb.ateam.exception.DataManagerException;
import cz.vutbr.fit.pdb.ateam.gui.LoginForm;
import cz.vutbr.fit.pdb.ateam.model.BaseModel;
import cz.vutbr.fit.pdb.ateam.model.employee.EmployeeModel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectTypeModel;
import cz.vutbr.fit.pdb.ateam.observer.AppStateChangedObservable;
import cz.vutbr.fit.pdb.ateam.observer.IModelChangedStateListener;
import cz.vutbr.fit.pdb.ateam.observer.ModelChangedStateObservable;
import cz.vutbr.fit.pdb.ateam.observer.SpatialObjectsReloadObservable;
import cz.vutbr.fit.pdb.ateam.tasks.AsyncTask;
import cz.vutbr.fit.pdb.ateam.utils.Logger;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
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
	protected static final int INFO_MESSAGE = 0;
	protected static final int WARNING_MESSAGE = 1;
	protected static final int ERROR_MESSAGE = 2;

	protected DataManager dataManager;
	protected JPanel rootPanel = null;

	protected AppStateChangedObservable appStateChangedObservable = AppStateChangedObservable.getInstance();

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
		reloadEmployees();
	}

	/**
	 * Saves list of models to the database based on {@link DataManager#saveModel(BaseModel)} method.
	 * After saving, notifies listeners which listens for {@link IModelChangedStateListener}.
	 * Listeners are notified only when model is actualy saved! (if is changed)
	 *
	 * @param models list of models to saveModel
	 */
	public void saveModels(final Collection<? extends BaseModel> models) {
		appStateChangedObservable.notifyStateChanged("Saving models...");

		new AsyncTask() {
			// models which were changed, these will be notified to listeners
			List<BaseModel> savedModels = new ArrayList<>();
			List<BaseModel> deletedModels = new ArrayList<>();

			@Override
			protected Boolean doInBackground() {

				for (BaseModel model : models) {
					try {
						if (model.isDeleted()) {
							dataManager.deleteModel(model);
							deletedModels.add(model); // error could only happen when exception
							continue;
						}

						if (dataManager.saveModel(model)) {
							savedModels.add(model);
						}
					} catch (DataManagerException e) {
						Logger.createLog(Logger.ERROR_LOG, e.getMessage());
						// TODO how to handle exceptions?
					}
				}

				// success if we saved at least one model
				return (deletedModels.size() + savedModels.size()) > 0;
			}

			@Override
			protected void onDone(boolean success) {
				ModelChangedStateObservable changedStateObservable = ModelChangedStateObservable.getInstance();
				if (!success) {
					showDialog(INFO_MESSAGE, "No data has changed!");
					appStateChangedObservable.notifyStateChanged("No data has changed!", true);
					return;
				}

				for (BaseModel model : deletedModels) {
					changedStateObservable.notifyObservers(model, IModelChangedStateListener.ModelState.DELETED);
				}

				for (BaseModel model : savedModels) {
					changedStateObservable.notifyObservers(model, IModelChangedStateListener.ModelState.SAVED);
				}

				Collections.sort(getSpatialObjects());

				appStateChangedObservable.notifyStateChanged("Data has been changed.", true);
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
			protected Boolean doInBackground() {
				try {
					dataManager.reloadAllSpatialObjects();
					dataManager.reloadAllSpatialObjectTypes();
					return true;
				} catch (DataManagerException e) {
					// TODO should check ORA-02396(max nevyuzity cas) or ORA-01012(neprihlaseno do systemu)
					Logger.createLog(Logger.ERROR_LOG, e.getMessage());
				}
				return false;
			}

			@Override
			protected void onDone(boolean success) {
				if (!success) {
					int errorCode = getErrorCode();
					if (errorCode == 2396 || errorCode == 1012) {
						LoginForm loginForm = new LoginForm();
						loginForm.setVisible(true);
						appStateChangedObservable.notifyStateChanged("Could not reload data.");
						return;
					}

					showDialog(ERROR_MESSAGE, "Can not reload data!");
					return;
				}

				SpatialObjectsReloadObservable.getInstance().notifyObservers();
			}
		};

		task.start();
	}

	/**
	 * Synchronouse method (available everywhere)
	 *
	 * @return cached spatial objects data from dataManager
	 */
	public List<SpatialObjectModel> getSpatialObjects() {
		return dataManager.getSpatialObjects();
	}

	/**
	 * Synchronouse method (available everywhere)
	 *
	 * @return cached spatial objects data from dataManager
	 */
	public List<SpatialObjectTypeModel> getSpatialObjectTypes() {
		return dataManager.getSpatialObjectTypes();
	}


	/**
	 * Asynchronously reloads data and store as cached data in dataManager.
	 * Data are accessible through {@link #getEmployees()} method
	 */
	public void reloadEmployees() {
		AsyncTask task = new AsyncTask() {
			@Override
			protected void onDone(boolean success) {
				SpatialObjectsReloadObservable.getInstance().notifyObservers();
			}//TODO change to EmployeeReloadObservable?

			@Override
			protected Boolean doInBackground() throws Exception {
				try {
					dataManager.reloadAllEmployees();
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
	 * @return cached employees data from dataManager
	 */
	public List<EmployeeModel> getEmployees() {
		return dataManager.getEmployees();
	}

	/**
	 * Displays dialog with given message in the middle of the given JPanel, based on type.
	 *
	 * @param type    type of the dialog (INFO_MESSAGE, WARNING_MESSAGE, ERROR_MESSAGE)
	 * @param message message with description of the error
	 */
	protected void showDialog(int type, String message) {
		String subject;
		int jOptPaneType;

		switch (type) {
			case INFO_MESSAGE:
				subject = "Info";
				jOptPaneType = JOptionPane.INFORMATION_MESSAGE;
				break;

			case WARNING_MESSAGE:
				subject = "Warning";
				jOptPaneType = JOptionPane.WARNING_MESSAGE;
				break;

			case ERROR_MESSAGE:
				subject = "Error";
				jOptPaneType = JOptionPane.ERROR_MESSAGE;
				break;

			default:
				return;
		}

		JOptionPane.showMessageDialog(rootPanel, message, subject, jOptPaneType);
	}

	/**
	 * Sets root panel for showing error dialog.
	 *
	 * @param rootPanel root JPanel
	 */
	protected void setRootPanel(JPanel rootPanel) {
		this.rootPanel = rootPanel;
	}
}
