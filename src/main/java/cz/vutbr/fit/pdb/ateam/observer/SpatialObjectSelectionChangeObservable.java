package cz.vutbr.fit.pdb.ateam.observer;

import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;

/**
 * @author Tomas Hanus
 * @author Tomas Mlynaric
 */
public class SpatialObjectSelectionChangeObservable extends SimpleObservable<SpatialObjectSelectionChangeObservable.SpatialObjectSelectionChangedListener> {
	private static SpatialObjectSelectionChangeObservable instance = new SpatialObjectSelectionChangeObservable();

	public synchronized static SpatialObjectSelectionChangeObservable getInstance() {
		return instance;
	}

	@Override
	public void notifyObservers(Object spatialObjectModel) {
		if (!(spatialObjectModel instanceof SpatialObjectModel))
			throw new ClassCastException("Observer must be type SpatialObjectModel");

		for (SpatialObjectSelectionChangedListener listener : getObservableList()) {
			listener.spatialObjectSelectionChangedListener((SpatialObjectModel) spatialObjectModel);
		}
	}

	public interface SpatialObjectSelectionChangedListener {
		/**
		 * Fires when spatial object is selected on zoo map canvas.
		 *
		 * @param spatialObjectModel selected spatial object model
		 */
		void spatialObjectSelectionChangedListener(SpatialObjectModel spatialObjectModel);
	}
}
