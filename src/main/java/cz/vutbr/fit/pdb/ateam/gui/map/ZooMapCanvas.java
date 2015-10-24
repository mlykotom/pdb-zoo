package cz.vutbr.fit.pdb.ateam.gui.map;

import cz.vutbr.fit.pdb.ateam.controller.Controller;
import cz.vutbr.fit.pdb.ateam.controller.ZooMapController;
import cz.vutbr.fit.pdb.ateam.gui.BasePanel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.utils.Utils;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Class paints spatial objects into JPanel, so user can better see
 * properties of the objects.
 *
 * @author Jakub Tutko
 * @author Tomas Mlynaric
 */
public class ZooMapCanvas extends BasePanel {
	public static final Color CANVAS_DEFAULT_COLOR = new Color(115, 239, 97);
	public static final int CANVAS_DEFAULT_WIDTH = 640;
	public static final int CANVAS_DEFAULT_HEIGHT = 480;
	public static final int UPDATE_AFTER_ACTION_DELAY_MILLIS = 500;

	private final ZooMapController controller;

	public ZooMapCanvas(ZooMapController controller) {
		this.controller = controller;
		initUI();
	}

	/**
	 * Initialize UI windows, elements and action listeners (mouse,..)
	 */
	public void initUI() {
		addMouseMotionListener(movingAdapter);
		addMouseListener(movingAdapter);
		addMouseWheelListener(new ScaleHandler());

		Utils.setComponentFixSize(this, CANVAS_DEFAULT_WIDTH, CANVAS_DEFAULT_HEIGHT);
		setBackground(CANVAS_DEFAULT_COLOR);

		// should be async task
		controller.reloadSpatialObjects();
	}

	/**
	 * Renders window with low level graphics
	 *
	 * @param g
	 */
	public void paint(Graphics g) {
		// TODO should return some error??
		if (controller.getSpatialObjects().isEmpty()) return;
		super.paint(g);

		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		for (SpatialObjectModel model : controller.getSpatialObjects()) {
			model.render(g2D);
		}
		g2D.setPaint(CANVAS_DEFAULT_COLOR);
	}

	public MouseAdapter movingAdapter = new MouseAdapter() {
		private int pressedX;
		private int pressedY;
		private int wholeDeltaX;
		private int wholeDeltaY;
		private SpatialObjectModel selectedObject;

		/**
		 * Choose object from canvas
		 *
		 * @param e
		 */
		public void mousePressed(MouseEvent e) {
			pressedX = e.getX();
			pressedY = e.getY();
			selectedObject = controller.selectObjectFromCanvas(pressedX, pressedY);
			wholeDeltaX = 0;
			wholeDeltaY = 0;
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

			wholeDeltaX += deltaX;
			wholeDeltaY += deltaY;

			selectedObject.moveOnCanvas(deltaX, deltaY);

			pressedX += deltaX;
			pressedY += deltaY;

			repaint();
		}

		/**
		 * Drops object on canvas & saves changes to spatialObject
		 * @param e
		 */
		public void mouseReleased(MouseEvent e) {
			if(selectedObject == null) return; // TODO this shouldn't happen
			selectedObject.rememberOrdinates(wholeDeltaX, wholeDeltaY);
			selectedObject.setIsChanged(true);
			selectedObject = null;
		}
	};

	@Override
	protected Controller getController() {
		return this.controller;
	}

	class ScaleHandler implements MouseWheelListener {
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
				selectedObject = controller.selectObjectFromCanvas(pointX, pointY);
				// second check - if we still didn't select any
				if (selectedObject == null) return;
			}

			selectedObject.scaleOnCanvas(e.getWheelRotation());
			repaint();
		}
	}
}
