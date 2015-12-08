package cz.vutbr.fit.pdb.ateam.observer;

import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.utils.Logger;

/**
 * Observer for spatial object reloading event
 *
 * @author Tomas Mlynaric
 */
public class SpatialObjectCreatingObservable extends SimpleObservable<ISpatialObjectCreatingListener> {
	private static SpatialObjectCreatingObservable instance = new SpatialObjectCreatingObservable();

	public synchronized static SpatialObjectCreatingObservable getInstance() {
		return instance;
	}

	public void notifyObservers(SpatialObjectModel.ModelShape type) {
		Logger.createLog(Logger.DEBUG_LOG, "Notifying that object is creating...");
		for (ISpatialObjectCreatingListener listener : getObservableList()) {
			listener.spatialObjectsCreatingListener(type);
		}
	}
}
