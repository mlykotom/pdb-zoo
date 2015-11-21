package cz.vutbr.fit.pdb.ateam.controller;

import cz.vutbr.fit.pdb.ateam.gui.tabs.SpatialObjectsTab;
import cz.vutbr.fit.pdb.ateam.gui.tabs.lists.SpatialObjectsList;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.observer.SpatialObjectSelectionChangeObservable;

/**
 * Created by Jakub on 24.10.2015.
 */
public class SpatialObjectTabController extends Controller implements SpatialObjectSelectionChangeObservable.SpatialObjectSelectionChangedListener {
	SpatialObjectsTab spatialObjectsTab;
	SpatialObjectsList spatialObjectList;
	ZooMapController zooMapController;

	public SpatialObjectTabController(SpatialObjectsTab spatialObjectsTab) {
		super();
		this.spatialObjectsTab = spatialObjectsTab;

		SpatialObjectSelectionChangeObservable.getInstance().subscribe(this);
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
