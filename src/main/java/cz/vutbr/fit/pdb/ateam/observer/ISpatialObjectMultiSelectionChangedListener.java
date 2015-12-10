package cz.vutbr.fit.pdb.ateam.observer;

import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;

import java.util.List;

/**
 * @author Tomas Mlynaric
 */
public interface ISpatialObjectMultiSelectionChangedListener {
	/**
	 * Fires when spatial object is selected on zoo map canvas.
	 *
	 * @param objects selected spatial object models
	 */
	void spatialObjectMultiSelectionChangedListener(List<SpatialObjectModel> objects);
}
