package cz.vutbr.fit.pdb.ateam.observer;

/**
 * Observer for spatial object reloading event
 *
 * @author Tomas Mlynaric
 */
public class SpatialObjectsReloadObservable extends SimpleObservable<SpatialObjectsReloadObservable.SpatialObjectsReloadListener> {
	private static SpatialObjectsReloadObservable instance = new SpatialObjectsReloadObservable();

	public synchronized static SpatialObjectsReloadObservable getInstance() {
		return instance;
	}

	@Override
	public void notifyObservers() {
		for (SpatialObjectsReloadListener listener : getObservableList()) {
			listener.spatialObjectsReloadListener();
		}
	}

	public interface SpatialObjectsReloadListener {
		/**
		 * Fires when spatial objects are reloaded
		 */
		void spatialObjectsReloadListener();
	}
}
