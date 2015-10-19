package controller;

import exception.DataManagerException;
import gui.ZooMapCanvas;
import model.SpatialObject;
import utils.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Jakub on 15.10.2015.
 */
public class ZooMapCanvasController extends Controller {
	private ZooMapCanvas canvas;

	public ZooMapCanvasController(ZooMapCanvas zooMapCanvas) {
		super();
		this.canvas = zooMapCanvas;

		ArrayList<SpatialObject> spatialObjects;

		try {
			spatialObjects = dataManager.getAllSpatialObjects();
		} catch (DataManagerException e) {
			Logger.createLog(Logger.ERROR_LOG, e.getMessage());
			return;
		}

		ArrayList<Shape> shapes = new ArrayList<>();

		for(SpatialObject object : spatialObjects) {
			shapes.add(object.getShape());
		}

		canvas.setShapes(shapes);

		canvas.repaint();
	}
}
