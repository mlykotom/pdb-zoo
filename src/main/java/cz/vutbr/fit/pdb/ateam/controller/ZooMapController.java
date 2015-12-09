package cz.vutbr.fit.pdb.ateam.controller;

import cz.vutbr.fit.pdb.ateam.exception.ModelException;
import cz.vutbr.fit.pdb.ateam.gui.map.ZooMapCanvas;
import cz.vutbr.fit.pdb.ateam.gui.map.ZooMapPanel;
import cz.vutbr.fit.pdb.ateam.model.BaseModel;
import cz.vutbr.fit.pdb.ateam.model.Coordinate;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialModelShape;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.observer.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

/**
 * Class controls all events occurred in ZooMapForm.
 *
 * @author Jakub Tutko
 * @author Tomas Mlynaric
 */
public class ZooMapController extends Controller implements ISpatialObjectsReloadListener, ISpatialObjectSelectionChangedListener, ISpatialObjectCreatingListener, IModelChangedStateListener {
	private ZooMapPanel form;
	private ZooMapCanvas canvas;
	private SpatialObjectModel selectedObjectOnCanvas;
	public SpatialObjectModel creatingModel;
	public MouseHandler mouseHandler = new MouseHandler();

	/**
	 * Enum for canvas modes.
	 * Canvas cursor is setup based on this mode
	 */
	public enum MouseMode {
		CREATING(new Cursor(Cursor.CROSSHAIR_CURSOR)),
		SELECTING(new Cursor(Cursor.HAND_CURSOR)),
		MERGING(new Cursor(Cursor.HAND_CURSOR));

		private Cursor cursor;

		MouseMode(Cursor c) {
			cursor = c;
		}

		public Cursor getCursor() {
			return cursor;
		}
	}

	/**
	 * Constructor saves instance of the ZooMapForm as local
	 * variable. This form is than used for all changes made
	 * in ZooMapForm.
	 *
	 * @param zooMapPanel instance of the ZooMapForm
	 */
	public ZooMapController(ZooMapPanel zooMapPanel) {
		super();
		SpatialObjectsReloadObservable.getInstance().subscribe(this);
		SpatialObjectSelectionChangeObservable.getInstance().subscribe(this);
		SpatialObjectCreatingObservable.getInstance().subscribe(this);
		ModelChangedStateObservable.getInstance().subscribe(this);
		this.form = zooMapPanel;
	}

	/**
	 * Assigns canvas and repaints it
	 *
	 * @return instantiated repainted canvas
	 */
	public ZooMapCanvas prepareAndRepaintCanvas() {
		this.canvas = new ZooMapCanvas(this);
		canvas.repaint();
		return canvas;
	}

	/**
	 * Iterates through whole list and checks if any of spatial objects is in point specified by ordinates.
	 * If more than one object on coordinate, selects latest in list
	 *
	 * @param pointedX x-coordinate on map
	 * @param pointedY y-coordinate on map
	 * @return spatial object|null
	 */
	public SpatialObjectModel getObjectFromCanvas(int pointedX, int pointedY) {
		SpatialObjectModel retObject = null;

		for (SpatialObjectModel spatialObject : getSpatialObjects()) {
			if (!spatialObject.isWithin(pointedX, pointedY)) {
				continue;
			}
			retObject = spatialObject;
		}

		return retObject;
	}

	/**
	 * Unselects any spatial objects and select specified one
	 *
	 * @param objectToSelect this object will be selected and notified that was selected to all listeners
	 */
	public void selectSpatialObject(SpatialObjectModel objectToSelect) {
		// twice clicked on map (no object selected)
		if (objectToSelect == null && this.selectedObjectOnCanvas == null) return;
		// twice clicked on the same object
		if (objectToSelect != null && objectToSelect.equals(this.selectedObjectOnCanvas)) return;

		// unselects all objects
		for (SpatialObjectModel object : getSpatialObjects()) {
			object.selectOnCanvas(false);
		}

		SpatialObjectSelectionChangeObservable.getInstance().notifyObservers(objectToSelect);
	}

	// ----------------------
	// ------------- HANDLERS
	// ----------------------

	/**
	 * Handler when mouse is scrolled in canvas - handles scaling spatial objects
	 */
	public MouseWheelListener scaleHandler = new MouseWheelListener() {
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

	/**
	 * Handler for mouse movement in canvas - handles moving objects
	 */
	public class MouseHandler extends MouseAdapter {
		private int pressedX;
		private int pressedY;
		private MouseMode mode = MouseMode.SELECTING;
		private SpatialObjectModel selectedObject;
		private SpatialModelShape creatingModelShape;
		private int mouseClickCount = 0;
		private ArrayList<Coordinate> pressedCoordinates = new ArrayList<>();

		@Override
		public void mouseClicked(MouseEvent mouseEvent) {
			pressedX = mouseEvent.getX();
			pressedY = mouseEvent.getY();

			pressedCoordinates.add(new Coordinate(pressedX, pressedY));
			mouseClickCount++;

			switch (mode) {
				case CREATING:
					try {
						// only count points
						if (mouseClickCount < creatingModelShape.getPointsToRenderCount()) {
							return;
						}

						creatingModel = SpatialObjectModel.create(creatingModelShape, pressedCoordinates);
					} catch (ModelException e) {
						e.printStackTrace();
					}
					break;
			}

			canvas.repaint();
		}

		/**
		 * Choose object from canvas
		 *
		 * @param mouseEvent
		 */
		public void mousePressed(MouseEvent mouseEvent) {
			if (mode != MouseMode.SELECTING) return;

			pressedX = mouseEvent.getX();
			pressedY = mouseEvent.getY();

			// creating has more priority than selecting
			selectedObject = getObjectFromCanvas(pressedX, pressedY);
			// selected object may be null, in this case every object is unselected
			selectSpatialObject(selectedObject);

			canvas.repaint();
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
			selectedObject = null;
		}

		public void setMode(MouseMode mode) {
			this.mode = mode;
			canvas.setCursor(mode.getCursor());
		}

		/**
		 * Creating new shape
		 *
		 * @param creatingModelShape this shape will be set for creating
		 */
		public void setCreatingModel(SpatialModelShape creatingModelShape) {
			this.clearCreatingMode();
			this.creatingModelShape = creatingModelShape;
			this.setMode(MouseMode.CREATING);
		}

		public void finishCreatingAndSetSelectingModel() {
			if (creatingModel != null) {
				getSpatialObjects().add(creatingModel);
				SpatialObjectsReloadObservable.getInstance().notifyObservers();
			}

			this.clearCreatingMode();
			this.setMode(MouseMode.SELECTING);
		}

		public void clearCreatingMode() {
			pressedCoordinates.clear();
			this.creatingModelShape = null;
			creatingModel = null;
			mouseClickCount = 0;
		}
	}

	// --------------------------
	// ------------- FORM ACTIONS
	// --------------------------

	/**
	 * Action when clicked on saveModel button
	 */
	public void saveSpatialObjectsAction() {
		mouseHandler.finishCreatingAndSetSelectingModel();
		saveModels(getSpatialObjects());
	}

	/**
	 * Cancel any changes made to spatial objects by reloading all objects from DB
	 */
	public void cancelChangedSpatialObjectsAction() {
		mouseHandler.clearCreatingMode();
		reloadSpatialObjects();
	}

	// -----------------------
	// ------------- LISTENERS
	// -----------------------

	/**
	 * Fires when spatial objects are reloaded
	 */
	@Override
	public void spatialObjectsReloadListener() {
		if (canvas == null) return;
		canvas.repaint();
	}

	/**
	 * Fires when spatial object is selected on zoo map canvas.
	 *
	 * @param spatialObjectModel selected spatial object model
	 */
	@Override
	public void spatialObjectSelectionChangedListener(SpatialObjectModel spatialObjectModel) {
		if (spatialObjectModel == null) {
			this.selectedObjectOnCanvas = null;
			// unselects all objects
			for (SpatialObjectModel object : getSpatialObjects()) {
				object.selectOnCanvas(false);
			}
		} else {
			spatialObjectModel.selectOnCanvas(true);
			this.selectedObjectOnCanvas = spatialObjectModel;
		}
	}

	/**
	 * Fires when spatial objects are reloaded
	 *
	 * @param type which should be set for creating
	 */
	@Override
	public void spatialObjectsCreatingListener(SpatialModelShape type, boolean isFinished) {
		if(isFinished){
			mouseHandler.finishCreatingAndSetSelectingModel();
		}
		else {
			mouseHandler.setCreatingModel(type);
		}
	}

	/**
	 * Listener for any model saved to DB.
	 * Must have conditions on model type!
	 *
	 * @param model      any model saved to DB
	 * @param modelState specifies what happened to the model (possibly SAVED, DELETED) see {@link ModelState}
	 */
	public void modelChangedStateListener(BaseModel model, ModelState modelState) {
		if (!(model instanceof SpatialObjectModel)) return;
		canvas.repaint();
	}
}
