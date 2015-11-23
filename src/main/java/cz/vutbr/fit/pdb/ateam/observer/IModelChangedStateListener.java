package cz.vutbr.fit.pdb.ateam.observer;

import cz.vutbr.fit.pdb.ateam.model.BaseModel;

/**
 * @author Tomas Mlynaric
 */
public interface IModelChangedStateListener {
	/**
	 * Possible model states
	 */
	enum ModelState {
		SAVED,
		DELETED
	};

	/**
	 * Listener for any model saved to DB.
	 * Must have conditions on model type!
	 *
	 * @param model any model saved to DB
	 * @param modelState specifies what happened to the model (possibly SAVED, DELETED) see {@link ModelState}
	 */
	void modelChangedStateListener(BaseModel model, ModelState modelState);
}
