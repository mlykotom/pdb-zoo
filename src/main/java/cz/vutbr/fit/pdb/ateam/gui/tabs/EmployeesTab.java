package cz.vutbr.fit.pdb.ateam.gui.tabs;

import com.intellij.uiDesigner.core.GridLayoutManager;
import cz.vutbr.fit.pdb.ateam.controller.Controller;
import cz.vutbr.fit.pdb.ateam.controller.EmployeesTabController;
import cz.vutbr.fit.pdb.ateam.gui.BasePanel;
import cz.vutbr.fit.pdb.ateam.gui.ContentPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Tomas Mlynaric on 21.10.2015.
 */
public class EmployeesTab extends BasePanel {
	private final ContentPanel contentPanel;

	private final EmployeesTabController controller;

	private JPanel rootPanel;
	private JTabbedPane tabsPane;

	public EmployeesTab(ContentPanel mainPanel, JTabbedPane tabsPane) {
		this.tabsPane = tabsPane;
		this.controller = new EmployeesTabController(this);
		this.contentPanel = mainPanel;
		add(rootPanel);
	}

	@Override
	public Controller getController() {
		return this.controller;
	}


	public JTabbedPane getTabPanel() {
		return tabsPane;
	}

	{
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
		$$$setupUI$$$();
	}

	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$() {
		rootPanel = new JPanel();
		rootPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		rootPanel.setBackground(new Color(-1));
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return rootPanel;
	}
}
