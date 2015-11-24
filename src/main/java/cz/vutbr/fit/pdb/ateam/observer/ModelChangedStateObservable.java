package cz.vutbr.fit.pdb.ateam.observer;

import cz.vutbr.fit.pdb.ateam.model.BaseModel;

/**
 * Observer for spatial object reloading event
 *
 * @author Tomas Mlynaric
 */
public class ModelChangedStateObservable extends SimpleObservable<IModelChangedStateListener> {
	private static ModelChangedStateObservable instance = new ModelChangedStateObservable();

	public synchronized static ModelChangedStateObservable getInstance() {
		return instance;
	}

	public void notifyObservers(BaseModel model, IModelChangedStateListener.ModelState modelState) {
		for (IModelChangedStateListener listener : getObservableList()) {
			listener.modelChangedStateListener(model, modelState);
		}
	}
}
