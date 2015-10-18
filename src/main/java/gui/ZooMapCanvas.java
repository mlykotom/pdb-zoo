package gui;

import controller.ZooMapCanvasController;
import controller.ZooMapFormController;
import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;

/**
 * Created by Jakub on 15.10.2015.
 */
public class ZooMapCanvas extends JPanel {
	private final ZooMapCanvasController controller;

	public ZooMapCanvas() {
		this.controller = new ZooMapCanvasController(this);
		MouseMoveScale();
		Utils.setComponentFixSize(this, 800, 600);
	}

	private Rectangle2D.Float myRect = new Rectangle2D.Float(50, 50, 50, 50);

	public MovingAdapter ma = new MovingAdapter();

	public void MouseMoveScale() {
		addMouseMotionListener(ma);
		addMouseListener(ma);
		addMouseWheelListener(new ScaleHandler());
	}

	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2d = (Graphics2D) g;

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g2d.setColor(new Color(0, 0, 200));
		g2d.fill(myRect);
	}

	class MovingAdapter extends MouseAdapter {

		private int x;

		private int y;

		public void mousePressed(MouseEvent e) {
			x = e.getX();
			y = e.getY();
		}

		public void mouseDragged(MouseEvent e) {

			int dx = e.getX() - x;
			int dy = e.getY() - y;

			if (myRect.getBounds2D().contains(x, y)) {
				myRect.x += dx;
				myRect.y += dy;
				repaint();
			}
			x += dx;
			y += dy;
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
