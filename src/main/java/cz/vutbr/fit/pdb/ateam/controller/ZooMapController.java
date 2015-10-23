package cz.vutbr.fit.pdb.ateam.controller;

import cz.vutbr.fit.pdb.ateam.exception.DataManagerException;
import cz.vutbr.fit.pdb.ateam.gui.map.ZooMapCanvas;
import cz.vutbr.fit.pdb.ateam.gui.map.ZooMapPanel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.utils.Logger;

import java.util.ArrayList;

/**
 * Class controls all events occurred in ZooMapForm.
 *
 * @author Jakub Tutko
 */
public class ZooMapController extends Controller{
	private ZooMapPanel form;
	private ZooMapCanvas canvas;
	private ArrayList<SpatialObjectModel> spatialObjects = new ArrayList<>();

	//public HashMap<Long, SpatialObjectModel> spatialObjectsToUpdate = new HashMap<Long, SpatialObjectModel>();

	/**
	 * Constructor saves instance of the ZooMapForm as local
	 * variable. This form is than used for all changes made
	 * in ZooMapForm.
	 *
	 * @param zooMapPanel instance of the ZooMapForm
	 */
	public ZooMapController(ZooMapPanel zooMapPanel) {
		super();
		this.form = zooMapPanel;
	}

	/**
	 * Assigns canvas and repaints it
	 * @return instantiated repainted canvas
	 */
	public ZooMapCanvas prepareCanvas(){
		this.canvas = new ZooMapCanvas(this);
		canvas.repaint();
		return canvas;
	}

	/**
	 * Reloads data into the controller
	 * SHOULD BE ASYNC !!
	 * @return success flag
	 */
	public boolean reloadSpatialObjects(){
		try {
			spatialObjects = dataManager.getAllSpatialObjects();
			return true;
		} catch (DataManagerException e) {
			Logger.createLog(Logger.ERROR_LOG, e.getMessage());
			return false;
		}
	}

	/**
	 * Save one spatial object to DB
	 * @param model spatial object must be changed, otherwise skipped
	 * @throws DataManagerException
	 */
	public void saveSpatialObject(SpatialObjectModel model) throws DataManagerException {
		if(!model.isChanged()){
			Logger.createLog(Logger.DEBUG_LOG, String.format("Skipping updating model %d (not changed)", model.getId()));
			return;
		}
		dataManager.saveSpatial(model);
	}

	/**
	 * Action when clicked on save button
	 * @throws DataManagerException
	 */
	public void saveChangedSpatialObjectsAction() throws DataManagerException {
		for(SpatialObjectModel spatialObject : spatialObjects){
			saveSpatialObject(spatialObject);
		}
	}

	/**
	 * Iterates through whole list and checks if any of spatial objects is in point specified by ordinates
	 *
	 * @param pointedX
	 * @param pointedY
	 * @return
	 */
	public SpatialObjectModel selectObjectFromCanvas(int pointedX, int pointedY) {
		for (SpatialObjectModel spatialObject : spatialObjects) {
			if (!spatialObject.isWithin(pointedX, pointedY)) {
				continue;
			}

			return spatialObject;
		}

		return null;
	}


	public ArrayList<SpatialObjectModel> getSpatialObjects() {
		return spatialObjects;
	}
}
