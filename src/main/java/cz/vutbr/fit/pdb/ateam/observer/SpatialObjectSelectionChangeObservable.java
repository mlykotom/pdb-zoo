package cz.vutbr.fit.pdb.ateam.observer;

import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;

/**
 * @author Tomas Hanus
 * @author Tomas Mlynaric
 */
public class SpatialObjectSelectionChangeObservable extends SimpleObservable<ISpatialObjectSelectionChangedListener> {
	private static SpatialObjectSelectionChangeObservable instance = new SpatialObjectSelectionChangeObservable();

	public synchronized static SpatialObjectSelectionChangeObservable getInstance() {
		return instance;
	}

	@Override
	public void notifyObservers(Object spatialObjectModel) {
		if (!(spatialObjectModel instanceof SpatialObjectModel))
			throw new ClassCastException("Observer must be type SpatialObjectModel");

		for (ISpatialObjectSelectionChangedListener listener : getObservableList()) {
			listener.spatialObjectSelectionChangedListener((SpatialObjectModel) spatialObjectModel);
		}
	}
}
