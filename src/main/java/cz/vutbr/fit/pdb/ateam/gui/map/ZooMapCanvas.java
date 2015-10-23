package cz.vutbr.fit.pdb.ateam.gui.map;

import cz.vutbr.fit.pdb.ateam.controller.ZooMapController;
import cz.vutbr.fit.pdb.ateam.exception.DataManagerException;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.utils.Utils;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Class paints spatial objects into JPanel, so user can better see
 * properties of the objects.
 *
 * @author Jakub Tutko
 * @author Tomas Mlynaric
 */
public class ZooMapCanvas extends JPanel {
	public static final Color CANVAS_DEFAULT_COLOR = new Color(115, 239, 97);
	public static final int CANVAS_DEFAULT_WIDTH = 640;
	public static final int CANVAS_DEFAULT_HEIGHT = 480;
	public static final int UPDATE_AFTER_ACTION_DELAY_MILLIS = 500;

	private final ZooMapController controller;
	private HashSet<SpatialObjectModel> spatialObjectsToUpdate = new HashSet<>();

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
		private SpatialObjectModel selectedObject;
		private Timer MouseReleasedTimer;

		/**
		 * Choose object from canvas
		 *
		 * @param e
		 */
		public void mousePressed(MouseEvent e) {
			pressedX = e.getX();
			pressedY = e.getY();
			selectedObject = SpatialObjectModel.selectObjectFromCanvas(controller.getSpatialObjects(), pressedX, pressedY);
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

			repaint();
		}

		/**
		 * Drops object on canvas & saves changes to spatialObject
		 * TODO should check if is it really reliable (maybe can be situation when something breaks :( )
		 * @param e
		 */
		public void mouseReleased(MouseEvent e) {
			spatialObjectsToUpdate.add(selectedObject);
			selectedObject = null;

			// restarts timer
			if (MouseReleasedTimer != null && MouseReleasedTimer.isRunning()) {
				MouseReleasedTimer.stop();
			}

			// creates new timer which means that it will update object(s) after while
			MouseReleasedTimer = new Timer(UPDATE_AFTER_ACTION_DELAY_MILLIS, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionEvent) {
					try {
						// needs to be as iterator + cycle, cause we are removing items in cycle!
						Iterator<SpatialObjectModel> iterator = spatialObjectsToUpdate.iterator();
						while (iterator.hasNext()) {
							controller.updateSpatialObject(iterator.next());
							System.out.println("Updated succesfully");
							iterator.remove();
						}
					} catch (DataManagerException e1) {
						e1.printStackTrace();
						System.out.println("Unsuccesfull :(");
					}
				}
			});
			MouseReleasedTimer.setRepeats(false);
			MouseReleasedTimer.start();
		}
	};

	class ScaleHandler implements MouseWheelListener {
		private SpatialObjectModel selectedObject;

		private Timer wheelMovementTimer;

		public void mouseWheelMoved(MouseWheelEvent e) {
			if (e.getScrollType() != MouseWheelEvent.WHEEL_UNIT_SCROLL) {
				return;
			}

			// reset timer
			if(wheelMovementTimer != null && wheelMovementTimer.isRunning()) wheelMovementTimer.stop();

			int pointX = e.getX();
			int pointY = e.getY();

			// this allows to scale only objects within mouse pointer
			if (selectedObject != null && !selectedObject.isWithin(pointX, pointY)) {
				selectedObject = null;
			}

			// check if selected object to reduce operations
			if (selectedObject == null) {
				selectedObject = SpatialObjectModel.selectObjectFromCanvas(controller.getSpatialObjects(), pointX, pointY);
				// second check - if we still didn't select any
				if (selectedObject == null) return;
			}

			selectedObject.scaleOnCanvas(e.getWheelRotation());
			repaint();
		}
	}
}
