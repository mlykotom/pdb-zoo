package cz.vutbr.fit.pdb.ateam.controller;

import cz.vutbr.fit.pdb.ateam.gui.tabs.SpatialObjectsTab;
import cz.vutbr.fit.pdb.ateam.gui.tabs.lists.SpatialObjectsList;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.observer.ContentPanelObserverSubject;

/**
 * Created by Jakub on 24.10.2015.
 */
public class SpatialObjectTabController extends Controller implements ContentPanelObserverSubject.ObjectSelectionChangedListener {
	SpatialObjectsTab spatialObjectsTab;
	SpatialObjectsList spatialObjectList;
	ZooMapController zooMapController;

	public SpatialObjectTabController(SpatialObjectsTab spatialObjectsTab) {
		super();
		this.spatialObjectsTab = spatialObjectsTab;

		ContentPanelObserverSubject.getInstance().subscribeForSelectionChange(this);
	}

	@Override
	public void notifyObjectSelectionChanged(SpatialObjectModel spatialObjectModel) {
		System.out.println("PRD");
	}
}
