package cz.vutbr.fit.pdb.ateam.gui.map;

import cz.vutbr.fit.pdb.ateam.controller.Controller;
import cz.vutbr.fit.pdb.ateam.controller.ZooMapController;
import cz.vutbr.fit.pdb.ateam.gui.BasePanel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.utils.Utils;

import javax.swing.*;
import java.awt.*;

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

	private final ZooMapController controller;

	public ZooMapCanvas(ZooMapController controller) {
		this.controller = controller;
		initUI();
	}

	/**
	 * Initialize UI windows, elements and action listeners (mouse,..)
	 */
	public void initUI() {
		addMouseMotionListener(controller.mouseHandler);
		addMouseListener(controller.mouseHandler);
		addMouseWheelListener(controller.scaleHandler);

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
}
