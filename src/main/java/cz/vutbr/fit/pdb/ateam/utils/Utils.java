package cz.vutbr.fit.pdb.ateam.utils;

import cz.vutbr.fit.pdb.ateam.adapter.DataManager;

import javax.swing.*;
import java.awt.*;

/**
 * Class includes helpful static methods used in the whole application.
 *
 * @author Jakub Tutko
 */
public class Utils {

	/**
	 * Disconnects database and closes application.
	 */
	public static void closeApplication() {
		DataManager.getInstance().disconnectDatabase();
		Logger.createLog(Logger.DEBUG_LOG, "Closing application with System.exit(0).");
		System.exit(0);
	}

	/**
	 * Method set fixed size for the given JComponent.
	 *
	 * @param component JComponent to work with
	 * @param width final width of the component
	 * @param height final height of the component
	 */
	public static void setComponentFixSize( JComponent component ,int width, int height) {
		component.setMinimumSize(new Dimension(width, height));
		component.setMaximumSize(new Dimension(width, height));
		component.setPreferredSize(new Dimension(width, height));
	}
}
