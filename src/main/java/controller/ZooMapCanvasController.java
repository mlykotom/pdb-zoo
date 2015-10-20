package controller;

import exception.DataManagerException;
import gui.ZooMapCanvas;
import model.SpatialObjectModel;
import utils.Logger;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Jakub on 15.10.2015.
 */
public class ZooMapCanvasController extends Controller {
	private ZooMapCanvas canvas;

	public ZooMapCanvasController(ZooMapCanvas zooMapCanvas) {
		super();
		this.canvas = zooMapCanvas;

		ArrayList<SpatialObjectModel> spacialModels;

		try {
			spacialModels = dataManager.getAllSpatialObjects();
		} catch (DataManagerException e) {
			Logger.createLog(Logger.ERROR_LOG, e.getMessage());
			return;
		}

		ArrayList<Shape> shapes = new ArrayList<>();

		for(SpatialObjectModel model : spacialModels) {
			shapes.add(model.getShape());
		}

		canvas.setShapes(shapes);

		canvas.repaint();
	}
}
