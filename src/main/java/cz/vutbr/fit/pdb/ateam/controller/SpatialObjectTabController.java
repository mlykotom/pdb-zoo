package cz.vutbr.fit.pdb.ateam.controller;

import cz.vutbr.fit.pdb.ateam.gui.components.SpatialObjectsTable;
import cz.vutbr.fit.pdb.ateam.gui.help.CreatingBuildingHelper;
import cz.vutbr.fit.pdb.ateam.gui.tabs.SpatialObjectsTab;
import cz.vutbr.fit.pdb.ateam.gui.tabs.details.SpatialObjectDetail;
import cz.vutbr.fit.pdb.ateam.gui.tabs.lists.SpatialObjectsList;
import cz.vutbr.fit.pdb.ateam.model.BaseModel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectTypeModel;
import cz.vutbr.fit.pdb.ateam.observer.*;
import cz.vutbr.fit.pdb.ateam.utils.Utils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Jakub on 24.10.2015.
 */
public class SpatialObjectTabController
		extends
			Controller
		implements
			ISpatialObjectSelectionChangedListener,
			SpatialObjectTableController,
			ISpatialObjectsReloadListener,
			IModelChangedStateListener{

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
		List<SpatialObjectModel> models;

		// TODO fill models by parameter from controller not the other way
		models = getSpatialObjects();

		for (SpatialObjectModel model : models) {
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
		if(spatialObjectModel == null) {
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
		if(selectedObject == null)
			changePanelContentIntoList();
		else {
			long id = selectedObject.getId();
			selectedObject = null;
			for (SpatialObjectModel model : getSpatialObjects()) {
				if(model.getId().equals(id)) {
					selectedObject = model;
					return;
				}
			}

			if(selectedObject == null) {
				changePanelContentIntoList();
			}
		}
	}

	/**
	 * Saves selected object with new properties into database.
	 */
	public void detailSaveButtonAction() {
		if(selectedObject == null) return;

		SpatialObjectTypeModel selectedObjectType = selectedObject.getType();
		String typeName = spatialObjectDetail.getTypeComboBoxVallue();
		for (SpatialObjectTypeModel type: getSpatialObjectTypes()) {
			if(typeName.equals(type.getName())) {
				selectedObjectType.setId(type.getId());
				selectedObjectType.setName(typeName);
			}
		}

		selectedObject.setName(spatialObjectDetail.getNameTextFieldVallue());
		selectedObject.setSpatialObjectType(selectedObjectType);
		selectedObject.setIsChanged(true);
		saveModels(selectedObject);
	}

	public void detailCancelButtonAction() {
		SpatialObjectSelectionChangeObservable.getInstance().notifyObservers(null);
	}

	public void detailDeleteButtonAction(SpatialObjectModel model) {
		spatialObjectsTableDeleteAction(model);
	}

	public void createBuildingButton() {
		SpatialObjectModel.ModelType shapeType = spatialObjectList.getComboBoxValue();
		SpatialObjectCreatingObservable.getInstance().notifyObservers(shapeType);
		Utils.changePanelContent(spatialObjectsTab, new CreatingBuildingHelper(shapeType));
	}

	@Override
	public void modelChangedStateListener(BaseModel model, ModelState modelState) {
		if((model instanceof SpatialObjectModel) && (modelState == ModelState.SAVED)) {
			selectedObject = (SpatialObjectModel) model;

			changePanelContentIntoDetail(selectedObject);
		} else {
			changePanelContentIntoList();
		}
	}
}
