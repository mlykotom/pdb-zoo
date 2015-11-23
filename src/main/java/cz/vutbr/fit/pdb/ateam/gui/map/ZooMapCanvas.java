package cz.vutbr.fit.pdb.ateam.gui.map;

import cz.vutbr.fit.pdb.ateam.adapter.DataManager;
import cz.vutbr.fit.pdb.ateam.controller.Controller;
import cz.vutbr.fit.pdb.ateam.controller.ZooMapController;
import cz.vutbr.fit.pdb.ateam.exception.DataManagerException;
import cz.vutbr.fit.pdb.ateam.gui.BasePanel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialLineStringModel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectTypeModel;
import cz.vutbr.fit.pdb.ateam.observer.SpatialObjectsReloadObservable;
import cz.vutbr.fit.pdb.ateam.tasks.AsyncTask;
import cz.vutbr.fit.pdb.ateam.utils.Logger;
import cz.vutbr.fit.pdb.ateam.utils.Utils;
import javafx.util.Pair;
import oracle.spatial.geometry.JGeometry;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;

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

	public ZooMapCanvas(final ZooMapController controller) {
		this.controller = controller;
		initUI();

		// async reload spatial objects
		//controller.reloadSpatialObjects();
		// TODO reload is called in reloadAllData() when login success
	}

	/**
	 * Initialize UI windows, elements and action listeners (mouse,..)
	 */
	public void initUI() {
		addMouseMotionListener(controller.mouseHandler);
		addMouseListener(controller.mouseHandler);
		addMouseWheelListener(controller.scaleHandler);

		// sets default cursor
		this.setCursor(ZooMapController.MouseMode.SELECTING.getCursor());

		Utils.setComponentFixSize(this, CANVAS_DEFAULT_WIDTH, CANVAS_DEFAULT_HEIGHT);
		setBackground(CANVAS_DEFAULT_COLOR);
	}

	/**
	 * Renders window with low level graphics
	 *
	 * @param g
	 */
	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		if(!controller.getSpatialObjects().isEmpty()) {
			SpatialObjectModel renderLatest = null;
			for (SpatialObjectModel model : controller.getSpatialObjects()) {
				if(model.isSelected()){
					renderLatest = model;
					continue;
				}
				model.render(g2D);
			}

			if(renderLatest != null){
				renderLatest.render(g2D);
			}

			g2D.setPaint(CANVAS_DEFAULT_COLOR);
		}
	}

	@Override
	public Controller getController() {
		return controller;
	}
}
