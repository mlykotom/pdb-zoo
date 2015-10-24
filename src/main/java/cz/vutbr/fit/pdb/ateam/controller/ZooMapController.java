package cz.vutbr.fit.pdb.ateam.controller;

import cz.vutbr.fit.pdb.ateam.exception.DataManagerException;
import cz.vutbr.fit.pdb.ateam.gui.map.ZooMapCanvas;
import cz.vutbr.fit.pdb.ateam.gui.map.ZooMapPanel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.observer.ContentPanelObserverSubject;
import cz.vutbr.fit.pdb.ateam.utils.Logger;

import java.awt.event.*;
import java.util.ArrayList;

/**
 * Class controls all events occurred in ZooMapForm.
 *
 * @author Jakub Tutko
 * @author Tomas Mlynaric
 */
public class ZooMapController extends Controller {
	private ZooMapPanel form;
	private ZooMapCanvas canvas;
	private ArrayList<SpatialObjectModel> spatialObjects = new ArrayList<>();
	private SpatialObjectModel selectedObjectOnCanvas;

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

	/**
	 * Cancel any changes made to spatial objects by reloading all objects from DB
	 */
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
	public SpatialObjectModel getObjectFromCanvas(int pointedX, int pointedY) {
		for (SpatialObjectModel spatialObject : spatialObjects) {
			if (!spatialObject.isWithin(pointedX, pointedY)) {
				continue;
			}

			return spatialObject;
		}

		return null;
	}

	/**
	 * Unselects any spatial objects and select specified one
	 *
	 * @param objectToSelect this object will be selected and notified that was selected to all listeners
	 */
	public void selectSpatialObject(SpatialObjectModel objectToSelect) {
		//if(objectToSelect.equals(this.selectedObjectOnCanvas)) return;

		// unselects all objects
		for (SpatialObjectModel object : getSpatialObjects()) {
			object.selectOnCanvas(false);
		}

		if(objectToSelect == null){
			form.getSelectedObjectWrapper().setVisible(false);
			form.getObjectName().setText("");
			this.selectedObjectOnCanvas = null;
			ContentPanelObserverSubject.getInstance().notifyAllObjectSelectionChangedListeners(null);
		}
		else {
			objectToSelect.selectOnCanvas(true);
			form.getObjectName().setText(objectToSelect.getId().toString());
			form.getSelectedObjectWrapper().setVisible(true);
			this.selectedObjectOnCanvas = objectToSelect;
			ContentPanelObserverSubject.getInstance().notifyAllObjectSelectionChangedListeners(objectToSelect);
		}
	}

	/**
	 * Handler for mouse movement in canvas - handles moving objects
	 */
	public MouseAdapter mouseHandler = new MouseAdapter() {
		private int pressedX;
		private int pressedY;
		private SpatialObjectModel selectedObject;

		@Override
		public void mouseClicked(MouseEvent mouseEvent) {
			pressedX = mouseEvent.getX();
			pressedY = mouseEvent.getY();
			selectedObject = getObjectFromCanvas(pressedX, pressedY);
			// selected object may be null, in this case every object is unselected
			selectSpatialObject(selectedObject);
			canvas.repaint();
		}

		/**
		 * Choose object from canvas
		 *
		 * @param mouseEvent
		 */
		public void mousePressed(MouseEvent mouseEvent) {
			pressedX = mouseEvent.getX();
			pressedY = mouseEvent.getY();
			selectedObject = getObjectFromCanvas(pressedX, pressedY);
		}

		/**
		 * Can drag objects on canvas
		 *
		 * @param mouseEvent
		 */
		public void mouseDragged(MouseEvent mouseEvent) {
			if (selectedObject == null) return;

			int deltaX = mouseEvent.getX() - pressedX;
			int deltaY = mouseEvent.getY() - pressedY;

			selectedObject.moveOnCanvas(deltaX, deltaY);

			pressedX += deltaX;
			pressedY += deltaY;

			canvas.repaint();
		}

		/**
		 * Drops object on canvas & saves changes to spatialObject
		 *
		 * @param mouseEvent
		 */
		public void mouseReleased(MouseEvent mouseEvent) {
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

		public void mouseWheelMoved(MouseWheelEvent mouseEvent) {
			if (mouseEvent.getScrollType() != MouseWheelEvent.WHEEL_UNIT_SCROLL) {
				return;
			}

			int pointX = mouseEvent.getX();
			int pointY = mouseEvent.getY();

			// this allows to scale only objects within mouse pointer
			if (selectedObject != null && !selectedObject.isWithin(pointX, pointY)) {
				selectedObject = null;
			}

			// check if selected object to reduce operations
			if (selectedObject == null) {
				selectedObject = getObjectFromCanvas(pointX, pointY);
				// second check - if we still didn't select any
				if (selectedObject == null) return;
			}

			int scaleDelta = mouseEvent.getWheelRotation();
			selectedObject.scaleOnCanvas(scaleDelta);
			canvas.repaint();
		}
	};

	public ArrayList<SpatialObjectModel> getSpatialObjects() {
		return spatialObjects;
	}
}
