package cz.vutbr.fit.pdb.ateam.controller;

import cz.vutbr.fit.pdb.ateam.adapter.DataManager;
import cz.vutbr.fit.pdb.ateam.exception.DataManagerException;
import cz.vutbr.fit.pdb.ateam.gui.components.SpatialDetailTable;
import cz.vutbr.fit.pdb.ateam.gui.components.SpatialObjectsTable;
import cz.vutbr.fit.pdb.ateam.gui.help.CreatingBuildingHelper;
import cz.vutbr.fit.pdb.ateam.gui.tabs.SpatialObjectsTab;
import cz.vutbr.fit.pdb.ateam.gui.tabs.details.SpatialObjectDetail;
import cz.vutbr.fit.pdb.ateam.gui.tabs.lists.SpatialObjectsList;
import cz.vutbr.fit.pdb.ateam.model.BaseModel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialModelShape;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.observer.*;
import cz.vutbr.fit.pdb.ateam.tasks.AsyncTask;
import cz.vutbr.fit.pdb.ateam.utils.Logger;
import cz.vutbr.fit.pdb.ateam.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for spatial objects tab
 *
 * @author Jakub Tutko
 * @author Tomas Mlynaric
 */
public class SpatialObjectTabController extends Controller
		implements
		ISpatialObjectSelectionChangedListener,
		SpatialObjectTableController,
		ISpatialObjectsReloadListener,
		IModelChangedStateListener {

	SpatialObjectsTab spatialObjectsTab;
	SpatialObjectsList spatialObjectList;
	SpatialObjectDetail spatialObjectDetail;
	SpatialObjectModel selectedObject;

	/**
	 * Constructor creates to separated views. One is for showing table with all spatial
	 * objects saved in the database and second is for showing and editing details about
	 * one specific spatial object. View with table is displayed.
	 *
	 * @param spatialObjectsTab instance of the spatial objects tab view
	 */
	public SpatialObjectTabController(SpatialObjectsTab spatialObjectsTab) {
		super();
		this.spatialObjectsTab = spatialObjectsTab;
		this.spatialObjectList = new SpatialObjectsList(this);
		this.spatialObjectDetail = new SpatialObjectDetail(this);

		SpatialObjectSelectionChangeObservable.getInstance().subscribe(this);
		SpatialObjectsReloadObservable.getInstance().subscribe(this);
		ModelChangedStateObservable.getInstance().subscribe(this);

		changePanelContentIntoList();

		setRootPanel(spatialObjectsTab);
	}

	/**
	 * Method changes content of the spatial objects tab to table with all spatial objects
	 * saved in the database. Spatial objects to fill up table are cached in the application.
	 */
	private void changePanelContentIntoList() {
		Utils.changePanelContent(spatialObjectsTab, spatialObjectList);
		fillUpSpatialObjectsTable();
		selectedObject = null;
	}

	/**
	 * Method changes content of the spatial objects tab to details about specific spacial object.
	 * Selected object's properties can be changed in this view.
	 *
	 * @param spatialObjectModel selected spatial object
	 */
	private void changePanelContentIntoDetail(SpatialObjectModel spatialObjectModel) {
		Utils.changePanelContent(spatialObjectsTab, spatialObjectDetail);
		spatialObjectDetail.setTypeComboBoxModel(getSpatialObjectTypes());
		spatialObjectDetail.setSpatialObject(spatialObjectModel);
		selectedObject = spatialObjectModel;
	}

	/**
	 * Filling up data table in the spatial object tab
	 */
	private void fillUpSpatialObjectsTable() {
		SpatialObjectsTable table = new SpatialObjectsTable(this);

		for (SpatialObjectModel model : getSpatialObjects()) {
			table.addSpatialObjectModel(model);
		}

		spatialObjectList.setSpatialObjectsTable(table);
	}

	/**
	 * Action notifies all listeners, even actual instance, that some Spatial Object
	 * from the table is selected.
	 *
	 * @param spatialObjectModel spatial object, which is selected in the table
	 */
	@Override
	public void spatialObjectsTableEditAction(SpatialObjectModel spatialObjectModel) {
		SpatialObjectSelectionChangeObservable.getInstance().notifyObservers(spatialObjectModel);
	}


	/**
	 * Action removes specific spatial object selected in the spatial objects table.
	 *
	 * @param spatialObjectModel spatial object, which is selected in the table
	 */
	@Override
	public void spatialObjectsTableDeleteAction(SpatialObjectModel spatialObjectModel) {
		spatialObjectModel.setDeleted(true);
		saveModels(spatialObjectModel);
		selectedObject = null;
	}

	/**
	 * Fires when spatial object is selected on zoo map canvas.
	 *
	 * @param spatialObjectModel selected spatial object model
	 */
	@Override
	public void spatialObjectSelectionChangedListener(SpatialObjectModel spatialObjectModel) {
		if (spatialObjectModel == null) {
			changePanelContentIntoList();
		} else {
			changePanelContentIntoDetail(spatialObjectModel);
		}
	}

	/**
	 * Fires when spatial objects are reload from the database. Panel refresh data
	 * in the spatial object table or reload details about selected object, depending
	 * on actual panel view.
	 */
	@Override
	public void spatialObjectsReloadListener() {
		if (selectedObject == null)
			changePanelContentIntoList();
		else {
			long id = selectedObject.getId();
			selectedObject = null;
			for (SpatialObjectModel model : getSpatialObjects()) {
				if (model.getId().equals(id)) {
					selectedObject = model;
					return;
				}
			}

			if (selectedObject == null) {
				changePanelContentIntoList();
			}
		}
	}

	/**
	 * Saves selected object with new properties into database.
	 */
	public void detailSaveButtonAction() {
		if (selectedObject == null) return;
		selectedObject.setzIndex(spatialObjectDetail.getZIndexSpinnerValue());
		selectedObject.setName(spatialObjectDetail.getNameTextFieldValue());
		selectedObject.setSpatialObjectType(spatialObjectDetail.getTypeComboBoxVallue());
		selectedObject.setIsChanged(true);
		saveModels(selectedObject);
	}

	/**
	 * Action for the cancel button in the spatial objects detailed view. Action
	 * notifies all other controllers that selected object is no more selected.
	 */
	public void detailCancelButtonAction() {
		SpatialObjectSelectionChangeObservable.getInstance().notifyObservers(null);
	}

	/**
	 * Action for the delete button in the spatial objects detailed view. Action
	 * deletes selected object.
	 *
	 * @param model model which will be deleted
	 */
	public void detailDeleteButtonAction(SpatialObjectModel model) {
		spatialObjectsTableDeleteAction(model);
	}

	/**
	 * Action when clicking on creating building button.
	 * Shows helper page with finish,cancel button + notifies about attempt of creation
	 */
	public void createBuildingButtonAction() {
		SpatialModelShape shapeType = spatialObjectList.getComboBoxValue();
		SpatialObjectCreatingObservable.getInstance().notifyObservers(shapeType, false);
		Utils.changePanelContent(spatialObjectsTab, new CreatingBuildingHelper(shapeType));
	}

	/**
	 * Fires when some model has changed.
	 *
	 * @param model      any model saved to DB
	 * @param modelState specifies what happened to the model (possibly SAVED, DELETED) see {@link ModelState}
	 */
	@Override
	public void modelChangedStateListener(BaseModel model, ModelState modelState) {
		if ((model instanceof SpatialObjectModel) && (modelState == ModelState.SAVED)) {
			selectedObject = (SpatialObjectModel) model;

			changePanelContentIntoDetail(selectedObject);
		} else {
			changePanelContentIntoList();
		}
	}

	/**
	 * Recalculates area and length of specified object
	 */
	public void recalculateShapeInfoAction() {
		new AsyncTask() {
			private double calculatedArea = 0.0;
			private double calculatedLength = 0.0;

			@Override
			protected Boolean doInBackground() {
				try {
					double[] shapeInfo = dataManager.getSpatialObjectAnalyticFunction(selectedObject);
					calculatedArea = shapeInfo[0];
					calculatedLength = shapeInfo[1];
					return true;
				} catch (DataManagerException e) {
					Logger.createLog(Logger.ERROR_LOG, e.getMessage());
					return false;
				}
			}

			@Override
			protected void onDone(boolean success) {
				if (success) {
					spatialObjectDetail.setEnableControlComponents(!selectedObject.isNew());
					spatialObjectDetail.setCalculatedInfo(calculatedArea, calculatedLength);
				} else {
					showDialog(ERROR_MESSAGE, "Can not calculate data!");
				}
			}
		}.start();
	}

	/**
	 * Recalculates distance to specified object
	 *
	 * @param spatialObjectTo {@link SpatialObjectModel} which will be counted distance to
	 */
	public void recalculateDistanceToObjectAction(final SpatialObjectModel spatialObjectTo) {
		new AsyncTask() {
			private double calculatedDistance = 0.0;

			@Override
			protected Boolean doInBackground() {
				try {
					calculatedDistance = dataManager.getDistanceToOtherSpatialObject(selectedObject, spatialObjectTo);
					return true;
				} catch (DataManagerException e) {
					Logger.createLog(Logger.ERROR_LOG, e.getMessage());
					return false;
				}
			}

			@Override
			protected void onDone(boolean success) {
				if (success) {
					spatialObjectDetail.setEnableControlComponents(!selectedObject.isNew());
					spatialObjectDetail.setCalculatedDistanceTo(calculatedDistance);
					SpatialObjectMultiSelectionChangeObservable.getInstance().notifyObservers(spatialObjectTo);
				} else {
					showDialog(ERROR_MESSAGE, "Can not calculate data!");
				}
			}
		}.start();
	}

	/**
	 * Selects any objects within specified distance
	 *
	 * @param distance boundary specified in meters
	 */
	public void selectAllWithinDistance(final Integer distance) {
		new AsyncTask() {
			private List<SpatialObjectModel> selectedObjects = new ArrayList<>();

			@Override
			protected Boolean doInBackground() {
				try {
					selectedObjects = dataManager.getAllSpatialObjectsFromFunction(DataManager.SQL_FUNCTION_WITHIN_DISTANCE, selectedObject, distance);
					return true;
				} catch (DataManagerException e) {
					e.printStackTrace();
					// TODO
					return false;
				}
			}

			@Override
			protected void onDone(boolean success) {
				SpatialObjectMultiSelectionChangeObservable.getInstance().notifyObservers(selectedObjects);
			}
		}.start();
	}

	/**
	 * Spatial operator for selecting closest N objects
	 * @param count how many closest objects will be selected
	 * @param isSameType whether will be selected only objects with the same spatial type
	 */
	public void selectClosestN(final Integer count, final boolean isSameType) {
		spatialObjectDetail.getSpatialDetailTable().clearModels();
		new AsyncTask() {
			private List<SpatialObjectModel> selectedObjects = new ArrayList<>();

			@Override
			protected Boolean doInBackground() {
				try {
					selectedObjects = dataManager.getClosestNSpatialObjects(selectedObject, count, isSameType);
					return true;
				} catch (DataManagerException e) {
					e.printStackTrace();
					// TODO
					return false;
				}
			}

			@Override
			protected void onDone(boolean success) {
				spatialObjectDetail.getSpatialDetailTable().setModels(selectedObjects);
				SpatialObjectMultiSelectionChangeObservable.getInstance().notifyObservers(selectedObjects);
			}
		}.start();
	}
}
