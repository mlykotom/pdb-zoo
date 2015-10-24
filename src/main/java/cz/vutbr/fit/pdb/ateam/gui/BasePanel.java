package cz.vutbr.fit.pdb.ateam.gui;

import cz.vutbr.fit.pdb.ateam.controller.Controller;

import javax.swing.*;

/**
 * Generic Panel contains abstract class getController to ensure that every Panel will contain a controller.
 *
 * @Author Tomas Hanus
 */
public abstract class BasePanel extends JPanel {

	/**
	 * Allows to get to the parent controller
	 *
	 * @return Controller for specific Panel
	 */
	public abstract Controller getController();
}
