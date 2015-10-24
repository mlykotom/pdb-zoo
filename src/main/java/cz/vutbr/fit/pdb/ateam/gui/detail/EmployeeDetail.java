package cz.vutbr.fit.pdb.ateam.gui.detail;

import cz.vutbr.fit.pdb.ateam.controller.Controller;
import cz.vutbr.fit.pdb.ateam.gui.BasePanel;
import cz.vutbr.fit.pdb.ateam.gui.ContentPanel;

import javax.swing.*;

/**
 * Created by Tomas on 10/24/2015.
 */
public class EmployeeDetail extends BasePanel{
	private JPanel mainPanel;
	private JTextField textField1;
	private JTextField textField2;
	private JTextField textField3;
	private JButton saveButton;
	private JButton discardButton;

	public EmployeeDetail(ContentPanel mainPanel) {
		this.mainPanel = mainPanel;

	}

	private void createUIComponents() {
		// TODO: place custom component creation code here
	}

	@Override
	protected Controller getController() {
		return null;
	}
}
