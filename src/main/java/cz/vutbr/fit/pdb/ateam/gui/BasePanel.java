package cz.vutbr.fit.pdb.ateam.gui;

import cz.vutbr.fit.pdb.ateam.controller.Controller;

import javax.swing.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Generic Panel contains abstract class getController to ensure that every Panel will contain a controller.
 *
 * @Author Tomas Hanus
 */
public abstract class BasePanel extends JPanel {
	protected static NumberFormat doubleFormatter = new DecimalFormat("#0.00");

	/**
	 * Allows to get to the parent controller
	 *
	 * @return Controller for specific Panel
	 */
	public abstract Controller getController();
}
