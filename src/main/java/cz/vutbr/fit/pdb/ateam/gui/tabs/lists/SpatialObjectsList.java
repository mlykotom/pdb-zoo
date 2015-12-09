package cz.vutbr.fit.pdb.ateam.gui.tabs.lists;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import cz.vutbr.fit.pdb.ateam.controller.Controller;
import cz.vutbr.fit.pdb.ateam.controller.SpatialObjectTabController;
import cz.vutbr.fit.pdb.ateam.gui.BasePanel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialModelShape;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Jakub on 24.10.2015.
 */
public class SpatialObjectsList extends BasePanel {
	private JPanel rootPanel;
	private JPanel createBuildingPanel;
	private JPanel tablePanel;
	private JComboBox<SpatialModelShape> shapeComboBox;
	private JButton createBuildingButton;
	private JTable spatialObjectsTable;
	private SpatialObjectTabController controller;

	public SpatialObjectsList(SpatialObjectTabController controller) {
		this.controller = controller;
		add(rootPanel);
		initUI();
	}

	private void initUI() {
		initComboBox();

		createBuildingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.createBuildingButton();
			}
		});
	}

	public SpatialModelShape getComboBoxValue() {
		return (SpatialModelShape) shapeComboBox.getSelectedItem();
	}

	private void initComboBox() {
		DefaultComboBoxModel<SpatialModelShape> comboBoxModel = (DefaultComboBoxModel<SpatialModelShape>) shapeComboBox.getModel();

		SpatialModelShape[] modelShapes = SpatialModelShape.class.getEnumConstants();

		for (SpatialModelShape modelShape : modelShapes) {
			comboBoxModel.addElement(modelShape);
		}

		shapeComboBox.setModel(comboBoxModel);
	}

	public void setSpatialObjectsTable(JTable table) {
		spatialObjectsTable = table;
		tablePanel.removeAll();
		tablePanel.add(new JScrollPane(spatialObjectsTable));
	}

	@Override
	public Controller getController() {
		return controller;
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
		rootPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
		createBuildingPanel = new JPanel();
		createBuildingPanel.setLayout(new GridLayoutManager(4, 5, new Insets(0, 0, 0, 0), -1, -1));
		rootPanel.add(createBuildingPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		final JLabel label1 = new JLabel();
		label1.setFont(new Font(label1.getFont().getName(), Font.BOLD, 20));
		label1.setText("Create New Building");
		createBuildingPanel.add(label1, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer1 = new Spacer();
		createBuildingPanel.add(spacer1, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JSeparator separator1 = new JSeparator();
		createBuildingPanel.add(separator1, new GridConstraints(2, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final JLabel label2 = new JLabel();
		label2.setFont(new Font(label2.getFont().getName(), Font.BOLD, 20));
		label2.setText("Buildings List");
		createBuildingPanel.add(label2, new GridConstraints(3, 0, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label3 = new JLabel();
		label3.setFont(new Font(label3.getFont().getName(), Font.BOLD, label3.getFont().getSize()));
		label3.setText("Building Shape:");
		createBuildingPanel.add(label3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		shapeComboBox = new JComboBox();
		createBuildingPanel.add(shapeComboBox, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer2 = new Spacer();
		createBuildingPanel.add(spacer2, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		createBuildingButton = new JButton();
		createBuildingButton.setFont(new Font(createBuildingButton.getFont().getName(), Font.BOLD, createBuildingButton.getFont().getSize()));
		createBuildingButton.setText("Create Building");
		createBuildingPanel.add(createBuildingButton, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(150, -1), new Dimension(150, -1), new Dimension(150, -1), 1, false));
		tablePanel = new JPanel();
		tablePanel.setLayout(new BorderLayout(0, 0));
		rootPanel.add(tablePanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return rootPanel;
	}
}
