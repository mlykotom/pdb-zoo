package cz.vutbr.fit.pdb.ateam.observer;

import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;

/**
 * @author Tomas Mlynaric
 */
public interface ISpatialObjectSelectionChangedListener {
	/**
	 * Fires when spatial object is selected on zoo map canvas.
	 *
	 * @param spatialObjectModel selected spatial object model
	 */
	void spatialObjectSelectionChangedListener(SpatialObjectModel spatialObjectModel);
}
