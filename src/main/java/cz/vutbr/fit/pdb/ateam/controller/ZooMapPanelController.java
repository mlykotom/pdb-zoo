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
public class ZooMapPanelController extends Controller{
	private ZooMapPanel form;
	private ZooMapCanvas canvas;
	private ArrayList<SpatialObjectModel> spatialObjects = new ArrayList<>();

	/**
	 * Constructor saves instance of the ZooMapForm as local
	 * variable. This form is than used for all changes made
	 * in ZooMapForm.
	 *
	 * @param zooMapPanel instance of the ZooMapForm
	 */
	public ZooMapPanelController(ZooMapPanel zooMapPanel) {
		super();
		this.form = zooMapPanel;
	}


	public void prepareCanvas(ZooMapCanvas canvas){
		this.canvas = canvas;
		canvas.repaint();
	}

	public void saveChangedObjectsAction(){

	}


	/**
	 * Reloads data into the cz.vutbr.fit.pdb.ateam.controller
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

	public void updateSpatialObject(SpatialObjectModel model) throws DataManagerException {
		dataManager.updateSpatial(model);
	}

	public ArrayList<SpatialObjectModel> getSpatialObjects() {
		return spatialObjects;
	}
}
