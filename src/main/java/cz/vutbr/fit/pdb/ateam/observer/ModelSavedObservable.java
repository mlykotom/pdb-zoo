package cz.vutbr.fit.pdb.ateam.observer;

import cz.vutbr.fit.pdb.ateam.model.BaseModel;

/**
 * Observer for spatial object reloading event
 *
 * @author Tomas Mlynaric
 */
public class ModelSavedObservable extends SimpleObservable<IModelChangedStateListener> {
	private static ModelSavedObservable instance = new ModelSavedObservable();

	public synchronized static ModelSavedObservable getInstance() {
		return instance;
	}

	public void notifyObservers(BaseModel model, IModelChangedStateListener.ModelState modelState) {
		for (IModelChangedStateListener listener : getObservableList()) {
			listener.modelChangedStateListener(model, modelState);
		}
	}
}
