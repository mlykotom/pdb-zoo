package cz.vutbr.fit.pdb.ateam.gui;

import cz.vutbr.fit.pdb.ateam.gui.map.ZooMapPanel;
import cz.vutbr.fit.pdb.ateam.gui.tabs.AnimalsTab;
import cz.vutbr.fit.pdb.ateam.gui.tabs.EmployeesTab;
import cz.vutbr.fit.pdb.ateam.gui.tabs.SpatialObjectsTab;

import javax.swing.*;
import java.awt.*;

/**
 * cz.vutbr.fit.pdb.ateam.Main panel with GUI form containing left panel (MAP) and right panel (TABS - Detail)
 * Created by Tomas Mlynaric on 20.10.2015.
 */
public class ContentPanel extends JPanel {
	private JPanel rootPanel;
	private JPanel detailWrapper;
	private JPanel mapWrapper;

	// TODO private + getters?
	public ZooMapPanel mapPanelContent;
	public SpatialObjectsTab spatialObjectsTab;
	public AnimalsTab animalsTab;
	public EmployeesTab employeesTab;

	public ContentPanel() {
		add(rootPanel);
		initUI();
	}

	/**
	 * Instantiates all panels and tabs
	 * (pointer to this is because it's possible they will have to communicate with each other)
	 */
	private void initUI() {
		// map panel (left)
		mapPanelContent = new ZooMapPanel(this);
		mapWrapper.add(mapPanelContent);

		final JTabbedPane detailTabsPane = new JTabbedPane();
		// tab panels (right)
		spatialObjectsTab = new SpatialObjectsTab(this);
		animalsTab = new AnimalsTab(this);
		employeesTab = new EmployeesTab(this, detailTabsPane);

		detailTabsPane.addTab("Objects", spatialObjectsTab);
		detailTabsPane.addTab("Animals", animalsTab);
		detailTabsPane.addTab("Employees", employeesTab);
		detailWrapper.add(detailTabsPane);

//		detailTabsPane.addChangeListener(new ChangeListener() {
//			@Override
//			public void stateChanged(ChangeEvent e) {
//				controller.tabChangeAction(detailTabsPane.getSelectedComponent());
//			}
//		});
	}

	public ZooMapPanel getMapPanelContent() {
		return mapPanelContent;
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
		rootPanel.setLayout(new BorderLayout(0, 0));
		mapWrapper = new JPanel();
		mapWrapper.setLayout(new BorderLayout(0, 0));
		rootPanel.add(mapWrapper, BorderLayout.WEST);
		detailWrapper = new JPanel();
		detailWrapper.setLayout(new BorderLayout(0, 0));
		rootPanel.add(detailWrapper, BorderLayout.CENTER);
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return rootPanel;
	}
}
