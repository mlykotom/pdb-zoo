package cz.vutbr.fit.pdb.ateam.controller;

import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;

/**
 * Created by Jakub on 25.10.2015.
 */
public interface SpatialObjectTableController {
	void spatialObjectsTableEditAction(SpatialObjectModel spatialObjectModel);
	void spatialObjectsTableDeleteAction(SpatialObjectModel spatialObjectModel);
}
