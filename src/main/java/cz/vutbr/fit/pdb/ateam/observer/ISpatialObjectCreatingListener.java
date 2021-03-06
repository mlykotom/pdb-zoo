package cz.vutbr.fit.pdb.ateam.observer;

import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialModelShape;

/**
 * @author Tomas Mlynaric
 */
public interface ISpatialObjectCreatingListener {
	/**
	 * Fires when spatial objects are reloaded
	 *
	 * @param type which should be set for creating
	 */
	void spatialObjectsCreatingListener(SpatialModelShape type, boolean isFinished);
}
