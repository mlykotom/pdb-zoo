package cz.vutbr.fit.pdb.ateam.controller;

import cz.vutbr.fit.pdb.ateam.adapter.DataManager;
import cz.vutbr.fit.pdb.ateam.exception.DataManagerException;
import cz.vutbr.fit.pdb.ateam.gui.components.SpatialObjectsTable;
import cz.vutbr.fit.pdb.ateam.gui.tabs.SpatialObjectsTab;
import cz.vutbr.fit.pdb.ateam.gui.tabs.details.SpatialObjectDetail;
import cz.vutbr.fit.pdb.ateam.gui.tabs.lists.SpatialObjectsList;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.observer.ISpatialObjectSelectionChangedListener;
import cz.vutbr.fit.pdb.ateam.observer.SpatialObjectSelectionChangeObservable;
import cz.vutbr.fit.pdb.ateam.utils.Logger;
import cz.vutbr.fit.pdb.ateam.utils.Utils;

import java.util.List;

/**
 * Created by Jakub on 24.10.2015.
 */
public class SpatialObjectTabController extends Controller implements ISpatialObjectSelectionChangedListener, SpatialObjectTableController {
	SpatialObjectsTab spatialObjectsTab;
	SpatialObjectsList spatialObjectList;
	SpatialObjectDetail spatialObjectDetail;

	public SpatialObjectTabController(SpatialObjectsTab spatialObjectsTab) {
		super();
		this.spatialObjectsTab = spatialObjectsTab;
		this.spatialObjectList = new SpatialObjectsList(this);
		this.spatialObjectDetail = new SpatialObjectDetail(this);

		Utils.changePanelContent(spatialObjectsTab, spatialObjectList);

		fillUpSpatialObjectsTable();

		SpatialObjectSelectionChangeObservable.getInstance().subscribe(this);
	}

	private void fillUpSpatialObjectsTable() {
		SpatialObjectsTable table = new SpatialObjectsTable(this);
		List<SpatialObjectModel> models;

		// TODO fill models by parameter from controller not the other way
		models = this.spatialObjectList.getController().getSpatialObjects();

		for (SpatialObjectModel model : models) {
			table.addSpatialObjectModel(model);
		}

		spatialObjectList.setSpatialObjectsTable(table);
	}

	/*@Override
	public void notifyObjectSelectionChanged(SpatialObjectModel spatialObjectModel) {
		System.out.println("PRD " + spatialObjectModel.getId());

		// TODO: change list with table into spatial object detail, if null received show table
	}*/

	@Override
	public void spatialObjectsTableEditAction(SpatialObjectModel spatialObjectModel) {
		System.out.println("EDIT: " + spatialObjectModel.getId());

		// TODO: notify that spatial object was selected
	}


	@Override
	public void spatialObjectsTableDeleteAction(SpatialObjectModel spatialObjectModel) {
		System.out.println("DELETE: " + spatialObjectModel.getId());

		// TODO: notify that data should be reloaded
	}

	/**
	 * Fires when spatial object is selected on zoo map canvas.
	 *
	 * @param spatialObjectModel selected spatial object model
	 */
	@Override
	public void spatialObjectSelectionChangedListener(SpatialObjectModel spatialObjectModel) {
		System.out.println("PRD");
	}
}
