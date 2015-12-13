package cz.vutbr.fit.pdb.ateam.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import cz.vutbr.fit.pdb.ateam.gui.map.ZooMapPanel;
import cz.vutbr.fit.pdb.ateam.gui.tabs.AnimalsTab;
import cz.vutbr.fit.pdb.ateam.gui.tabs.EmployeesTab;
import cz.vutbr.fit.pdb.ateam.gui.tabs.SpatialObjectsTab;

import javax.swing.*;
import java.awt.*;

/**
 * Main panel with GUI form containing left panel (MAP) and right panel (TABS - Detail)
 *
 * @author Tomas Mlynaric
 */
public class ContentPanel extends JPanel {
	private JPanel rootPanel;
	private JTabbedPane mapTabbedPane;
	private JTabbedPane detailTabbedPane;

	public ContentPanel() {
		add(rootPanel);
		initUI();
	}

	/**
	 * Instantiates all panels and tabs
	 * (pointer to this is because it's possible they will have to communicate with each other)
	 */
	private void initUI() {
		ZooMapPanel mapPanelContent = new ZooMapPanel(this);
		SpatialObjectsTab spatialObjectsTab = new SpatialObjectsTab(this);
		AnimalsTab animalsTab = new AnimalsTab(this, detailTabbedPane);
		EmployeesTab employeesTab = new EmployeesTab(this, detailTabbedPane);
		// map tabs
		mapTabbedPane.addTab("ZOO map", mapPanelContent);
		// detail tabs

		detailTabbedPane.addTab("Objects", spatialObjectsTab);

		JScrollPane animalScrolPane = new JScrollPane(animalsTab);
		animalScrolPane.getVerticalScrollBar().setUnitIncrement(16);
		detailTabbedPane.add(animalScrolPane, "Animals");

		JScrollPane employeeScrolPane = new JScrollPane(employeesTab);
		employeeScrolPane.getVerticalScrollBar().setUnitIncrement(16);
		detailTabbedPane.add(employeeScrolPane, "Employees");

		detailTabbedPane.setPreferredSize(new Dimension(600, 600));
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
		rootPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
		mapTabbedPane = new JTabbedPane();
		mapTabbedPane.setTabPlacement(1);
		rootPanel.add(mapTabbedPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		final JScrollPane scrollPane1 = new JScrollPane();
		scrollPane1.setVerticalScrollBarPolicy(20);
		rootPanel.add(scrollPane1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 500), null, 0, false));
		detailTabbedPane = new JTabbedPane();
		scrollPane1.setViewportView(detailTabbedPane);
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return rootPanel;
	}
}
