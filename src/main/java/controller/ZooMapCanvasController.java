package controller;

import exception.DataManagerException;
import gui.map.ZooMapCanvas;
import model.spatial.SpatialObjectModel;
import utils.Logger;

import java.util.ArrayList;

/**
 * Created by Jakub on 15.10.2015.
 */
public class ZooMapCanvasController extends Controller {
	private ZooMapCanvas canvas;
	private ArrayList<SpatialObjectModel> spacialObjects = new ArrayList<>();

	public ZooMapCanvasController(ZooMapCanvas zooMapCanvas) {
		super();
		try {
			spacialObjects = dataManager.getAllSpatialObjects();
		} catch (DataManagerException e) {
			Logger.createLog(Logger.ERROR_LOG, e.getMessage());
			return;
		}

		this.canvas = zooMapCanvas;
		canvas.repaint();
	}

	public ArrayList<SpatialObjectModel> getSpacialObjects() {
		return spacialObjects;
	}
}
