package cz.vutbr.fit.pdb.ateam.controller;

import cz.vutbr.fit.pdb.ateam.exception.DataManagerException;
import cz.vutbr.fit.pdb.ateam.exception.ModelException;
import cz.vutbr.fit.pdb.ateam.gui.map.ZooMapCanvas;
import cz.vutbr.fit.pdb.ateam.gui.map.ZooMapPanel;
import cz.vutbr.fit.pdb.ateam.model.BaseModel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.observer.*;
import oracle.spatial.geometry.JGeometry;

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
	public MouseHandler mouseHandler = new MouseHandler();

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
	 * Handler for mouse movement in canvas - handles moving objects
	 */
	public class MouseHandler extends MouseAdapter {
		private int pressedX;
		private int pressedY;
		private SpatialObjectModel selectedObject;
		private MouseMode mode = MouseMode.SELECTING;

		private SpatialObjectModel.ModelShape creatingModelShape;
		private int mouseClickCount = 0;
		private int oldPressedX;
		private int oldPressedY;

		private ArrayList<Cord> pressedCords = new ArrayList<>();
//		private ArrayList<Integer> pressedCoordsY = new ArrayList<>();

		class Cord {
			public int x;
			public int y;

			public Cord(int x, int y) {
				this.x = x;
				this.y = y;
			}

			public double[] toArray() {
				return new double[]{x, y};
			}

		}

		private JGeometry creatingGeometry;
		public SpatialObjectModel creatingModel;

		@Override
		public void mouseClicked(MouseEvent mouseEvent) {
			pressedX = mouseEvent.getX();
			pressedY = mouseEvent.getY();

			pressedCords.add(new Cord(pressedX, pressedY));
//			pressedCoordsY.add(pressedY);

			mouseClickCount++;

			switch (mode) {
				case CREATING:
					try {
					// only count points
					if (mouseClickCount < creatingModelShape.getPointsToRenderCount()) {
//							oldPressedX = pressedX;
//							oldPressedY = pressedY;
						return;
					}

					double[] points = new double[pressedCords.size() * 2];
					int i = 0;
					for (Cord cord : pressedCords) {
						points[i++] = cord.x;
						points[i++] = cord.y;
					}


					creatingGeometry = JGeometry.createLinearLineString(points, 2, 0);
					creatingGeometry.getElemInfo()[2] = 2;
					creatingModel = SpatialObjectModel.createFromJGeometry("<XXX>",  dataManager.getSpatialObjectType(21L), creatingGeometry);
//					getSpatialObjects().add(creatingModel);
//					creatingModel.setGeometry(creatingGeometry);

					// actually create object
//						JGeometry geom = SpatialObjectModel.createJGeometryFromModelType(creatingModelShape, pressedCoordsX, pressedCoordsY);
//						SpatialObjectModel newObject = SpatialObjectModel.createFromJGeometry("<<new>>", dataManager.getSpatialObjectType(21L), creatingGeometry); // TODO magic constant
//						getSpatialObjects().add(newObject);
//						SpatialObjectsReloadObservable.getInstance().notifyObservers();
//						oldPressedX = oldPressedY = mouseClickCount = 0;
//						mouseClickCount = 0;
//						pressedCords.clear();
//						pressedCoordsX.clear();
//						pressedCoordsY.clear();
//						setMode(MouseMode.SELECTING);
					} catch (DataManagerException | ModelException e) {
						e.printStackTrace();
					}
					break;

				case MERGING:
					break;

				case SELECTING:
					// note: selecting is handled in mousePress
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
			if (selectedObject == null) return; // TODO this shouldn't happen
			selectedObject = null;
		}

		public void setMode(MouseMode mode) {
			this.mode = mode;
			canvas.setCursor(mode.getCursor());
		}

		public void nullMouseClickCount() {
			mouseClickCount = 0;
		}

		public void setCreatingModelShape(SpatialObjectModel.ModelShape creatingModelShape) {
			this.creatingModelShape = creatingModelShape;
		}
	}


	// --------------------------
	// ------------- FORM ACTIONS
	// --------------------------

	/**
	 * Action when clicked on saveModel button
	 */
	public void saveSpatialObjectsAction() {
		saveModels(getSpatialObjects());
	}

	/**
	 * Cancel any changes made to spatial objects by reloading all objects from DB
	 */
	public void cancelChangedSpatialObjectsAction() {
		form.setUnselectedObject();
		reloadSpatialObjects();
	}

	/**
	 * Deletes selected model
	 */
	public void deleteSelectedModelsAction() {
		if (selectedObjectOnCanvas == null) return;
		selectedObjectOnCanvas.setDeleted(true);
		saveModels(selectedObjectOnCanvas); // TODO if want just delete without saving must implement reload canvas
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
			form.setUnselectedObject();
			this.selectedObjectOnCanvas = null;
			// unselects all objects
			for (SpatialObjectModel object : getSpatialObjects()) {
				object.selectOnCanvas(false);
			}
		} else {
			spatialObjectModel.selectOnCanvas(true);
			form.setSelecteObject(spatialObjectModel);
			this.selectedObjectOnCanvas = spatialObjectModel;
		}
	}

	/**
	 * Fires when spatial objects are reloaded
	 *
	 * @param type which should be set for creating
	 */
	@Override
	public void spatialObjectsCreatingListener(SpatialObjectModel.ModelShape type) {
		mouseHandler.setCreatingModelShape(type);
		mouseHandler.nullMouseClickCount();
		mouseHandler.setMode(MouseMode.CREATING);
	}

	/**
	 * Listener for any model saved to DB.
	 * Must have conditions on model type!
	 *
	 * @param model      any model saved to DB
	 * @param modelState specifies what happened to the model (possibly SAVED, DELETED) see {@link ModelState}
	 */
	public void modelChangedStateListener(BaseModel model, ModelState modelState) {
		if (!(model instanceof SpatialObjectModel)) return; // TODO for now we only handle SpatialObjects

		switch (modelState) {
			case DELETED:
				form.setUnselectedObject();
				canvas.repaint();
				break;

			case SAVED:
				if (((SpatialObjectModel) model).isSelected()) {
					form.setSelecteObject(model);
				}
				break;
		}
	}
}
