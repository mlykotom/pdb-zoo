package cz.vutbr.fit.pdb.ateam.controller;

import cz.vutbr.fit.pdb.ateam.exception.DataManagerException;
import cz.vutbr.fit.pdb.ateam.gui.map.ZooMapCanvas;
import cz.vutbr.fit.pdb.ateam.gui.map.ZooMapPanel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.utils.Logger;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Class controls all events occurred in ZooMapForm.
 *
 * @author Jakub Tutko
 */
public class ZooMapController extends Controller {
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
	 *
	 * @return instantiated repainted canvas
	 */
	public ZooMapCanvas prepareCanvas() {
		this.canvas = new ZooMapCanvas(this);
		canvas.repaint();
		return canvas;
	}

	/**
	 * Reloads data into the controller
	 * SHOULD BE ASYNC !!
	 *
	 * @return success flag
	 */
	public boolean reloadSpatialObjects() {
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
	 *
	 * @param model spatial object must be changed, otherwise skipped
	 * @throws DataManagerException
	 */
	public void saveSpatialObject(SpatialObjectModel model) throws DataManagerException {
		if (!model.isChanged()) {
			Logger.createLog(Logger.DEBUG_LOG, String.format("Skipping updating model %d (not changed)", model.getId()));
			return;
		}
		dataManager.saveSpatial(model);
	}

	/**
	 * Action when clicked on save button
	 *
	 * @throws DataManagerException
	 */
	public void saveChangedSpatialObjectsAction() throws DataManagerException {
		for (SpatialObjectModel spatialObject : spatialObjects) {
			saveSpatialObject(spatialObject);
		}
	}

	public void cancelChangedSpatialObjectsAction() {
		reloadSpatialObjects();
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


	/**
	 * Handler for mouse movement in canvas - handles moving objects
	 */
	public MouseAdapter mouseHandler = new MouseAdapter() {
		private int pressedX;
		private int pressedY;
		private SpatialObjectModel selectedObject;

		/**
		 * Choose object from canvas
		 *
		 * @param e
		 */
		public void mousePressed(MouseEvent e) {
			pressedX = e.getX();
			pressedY = e.getY();
			selectedObject = selectObjectFromCanvas(pressedX, pressedY);
		}

		/**
		 * Can drag objects on canvas
		 *
		 * @param e
		 */
		public void mouseDragged(MouseEvent e) {
			if (selectedObject == null) return;

			int deltaX = e.getX() - pressedX;
			int deltaY = e.getY() - pressedY;

			selectedObject.moveOnCanvas(deltaX, deltaY);

			pressedX += deltaX;
			pressedY += deltaY;

			canvas.repaint();
		}

		/**
		 * Drops object on canvas & saves changes to spatialObject
		 *
		 * @param e
		 */
		public void mouseReleased(MouseEvent e) {
			if (selectedObject == null) return; // TODO this shouldn't happen
			selectedObject = null;
		}
	};

	/**
	 * Handler when mouse is scrolled in canvas - handles scaling spatial objects
	 */
	public MouseWheelListener scaleHandler = new MouseWheelListener() {
		private static final int MOUSE_WHEEL_TIMER_DELAY = 500;
		private SpatialObjectModel selectedObject;

		public void mouseWheelMoved(MouseWheelEvent e) {
			if (e.getScrollType() != MouseWheelEvent.WHEEL_UNIT_SCROLL) {
				return;
			}

			int pointX = e.getX();
			int pointY = e.getY();

			// this allows to scale only objects within mouse pointer
			if (selectedObject != null && !selectedObject.isWithin(pointX, pointY)) {
				selectedObject = null;
			}

			// check if selected object to reduce operations
			if (selectedObject == null) {
				selectedObject = selectObjectFromCanvas(pointX, pointY);
				// second check - if we still didn't select any
				if (selectedObject == null) return;
			}

			int scaleDelta = e.getWheelRotation();
			selectedObject.scaleOnCanvas(scaleDelta);
			canvas.repaint();
		}
	};
}
