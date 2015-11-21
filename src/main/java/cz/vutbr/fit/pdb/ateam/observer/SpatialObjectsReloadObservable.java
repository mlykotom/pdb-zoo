package cz.vutbr.fit.pdb.ateam.observer;

/**
 * Observer for spatial object reloading event
 *
 * @author Tomas Mlynaric
 */
public class SpatialObjectsReloadObservable extends SimpleObservable<ISpatialObjectsReloadListener> {
	private static SpatialObjectsReloadObservable instance = new SpatialObjectsReloadObservable();

	public synchronized static SpatialObjectsReloadObservable getInstance() {
		return instance;
	}

	@Override
	public void notifyObservers() {
		for (ISpatialObjectsReloadListener listener : getObservableList()) {
			listener.spatialObjectsReloadListener();
		}
	}
}
