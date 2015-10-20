package gui;

import controller.ZooMapCanvasController;
import controller.ZooMapFormController;
import model.SpatialObjectModel;
import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Set;

/**
 * Class paints spatial objects into JPanel, so user can better see
 * properties of the objects.
 *
 * @author Jakub Tutko
 * @author Tomas Mlynaric
 */
public class ZooMapCanvas extends JPanel {
	private static final Color CANVAS_DEFAULT_COLOR = new Color(115, 239, 97);

	private final ZooMapCanvasController controller;
	private ArrayList<SpatialObjectModel> spatialObjects;

	public ZooMapCanvas() {
		this.controller = new ZooMapCanvasController(this);
		this.spatialObjects = controller.getSpacialObjects();
		initUI();
	}

	public void initUI() {
		addMouseMotionListener(ma);
		addMouseListener(ma);
		addMouseWheelListener(new ScaleHandler());

		Utils.setComponentFixSize(this, 800, 600);
		setBackground(CANVAS_DEFAULT_COLOR);
	}

	public void paint(Graphics g) {
		if(controller.getSpacialObjects().isEmpty()) return;

		super.paint(g);

		Graphics2D g2d = (Graphics2D) g;

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


		for(SpatialObjectModel model : this.spatialObjects) {
			Shape shape = model.getShape();
			g2d.setPaint(model.getType().getColor());
			g2d.fill(shape);
			g2d.draw(shape);
		}
		g2d.setPaint(CANVAS_DEFAULT_COLOR);
	}


	private Rectangle2D.Float myRect = new Rectangle2D.Float(50, 50, 50, 50);

	public MovingAdapter ma = new MovingAdapter();

	class MovingAdapter extends MouseAdapter {

		private int x;
		private int y;
		private Shape selectedShape;

		public void mousePressed(MouseEvent e) {
			x = e.getX();
			y = e.getY();


			for(SpatialObjectModel spatialObject : spatialObjects){
				Shape shape = spatialObject.getShape();
				if(shape.contains(x, y)){
					selectedShape = shape;
					break;
				}
			}
		}

		public void mouseDragged(MouseEvent e) {

			int dx = e.getX() - x;
			int dy = e.getY() - y;

			if (selectedShape.getBounds2D().contains(x, y)) {
				//selectedShape.x += dx;
				//selectedShape.y += dy;
				repaint();
			}
			x += dx;
			y += dy;
		}

		public void mouseReleased(MouseEvent e) {

		}
	}

	class ScaleHandler implements MouseWheelListener {

		public void mouseWheelMoved(MouseWheelEvent e) {

			int x = e.getX();
			int y = e.getY();

			if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {

				if (myRect.getBounds2D().contains(x, y)) {
					float amount = e.getWheelRotation() * 5f;
					myRect.width += amount;
					myRect.height += amount;
					repaint();
				}
			}
		}
	}
}
