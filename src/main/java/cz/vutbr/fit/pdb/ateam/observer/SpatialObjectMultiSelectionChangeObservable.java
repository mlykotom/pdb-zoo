package cz.vutbr.fit.pdb.ateam.observer;

import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;

import java.util.Collections;
import java.util.List;

/**
 * @author Tomas Mlynaric
 */
public class SpatialObjectMultiSelectionChangeObservable extends SimpleObservable<ISpatialObjectMultiSelectionChangedListener> {
	private static SpatialObjectMultiSelectionChangeObservable instance = new SpatialObjectMultiSelectionChangeObservable();

	public synchronized static SpatialObjectMultiSelectionChangeObservable getInstance() {
		return instance;
	}

	public void notifyObservers(List<SpatialObjectModel> objects) {
		for (ISpatialObjectMultiSelectionChangedListener listener : getObservableList()) {
			listener.spatialObjectMultiSelectionChangedListener(objects);
		}
	}

	public void notifyObservers(Object object) {
		notifyObservers(Collections.singletonList((SpatialObjectModel) object));
	}
}
