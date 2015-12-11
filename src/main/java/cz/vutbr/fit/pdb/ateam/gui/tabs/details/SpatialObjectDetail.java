package cz.vutbr.fit.pdb.ateam.gui.tabs.details;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import cz.vutbr.fit.pdb.ateam.controller.Controller;
import cz.vutbr.fit.pdb.ateam.controller.SpatialObjectTabController;
import cz.vutbr.fit.pdb.ateam.gui.BasePanel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectTypeModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * @author Jakub Tutko
 * @author Tomas Mlynaric
 */
public class SpatialObjectDetail extends BasePanel {
	private JPanel rootPanel;
	private JTextField idTextField;
	private JTextField nameTextField;
	private JComboBox<SpatialObjectTypeModel> typeComboBox;
	private JButton saveButton;
	private JButton cancelButton;
	private JButton deleteButton;
	private JButton calculateShapeInfoButton;
	private JLabel shapeAreaLabel;
	private JPanel areaWrapper;
	private JLabel shapeLengthLabel;
	private JComboBox<SpatialObjectModel> spatialObjectsComboBox;
	private JLabel distanceToObjectLabel;
	private JButton calculateDistanceButton;
	private JLabel squareMetersLabel;
	private JSpinner closestObjectsDistanceSpinner;
	private JButton showOnMapButton;
	private JSpinner zIndexSpinner;
	private SpatialObjectTabController controller;
	private SpatialObjectModel spatialObject;

	public SpatialObjectDetail(SpatialObjectTabController controller) {
		this.controller = controller;
		add(rootPanel);
		initUI();
	}

	public void setSpatialObject(SpatialObjectModel spatialObject) {
		this.spatialObject = spatialObject;

		idTextField.setText(spatialObject.getId().toString());
		nameTextField.setText(spatialObject.getName());
		typeComboBox.setSelectedItem(spatialObject.getType());
	}

	public String getNameTextFieldValue() {
		return nameTextField.getText();
	}

	public void setzIndexSpinnerValue(Integer value) {
		zIndexSpinner.setValue(value);
	}

	public Integer getZIndexSpinnerValue() {
		return (Integer) zIndexSpinner.getValue();
	}

	public SpatialObjectTypeModel getTypeComboBoxVallue() {
		return (SpatialObjectTypeModel) typeComboBox.getSelectedItem();
	}

	public void setTypeComboBoxModel(List<SpatialObjectTypeModel> objectTypes) {
		typeComboBox.removeAllItems();
		for (SpatialObjectTypeModel type : objectTypes) {
			typeComboBox.addItem(type);
		}
	}

	/**
	 * Set calculated informations and enable state for button
	 *
	 * @param area
	 * @param length
	 */
	public void setCalculatedInfo(Double area, Double length) {
		shapeAreaLabel.setText(area == null ? "--" : doubleFormatter.format(area));
		shapeLengthLabel.setText(length == null ? "--" : doubleFormatter.format(length));
	}


	public void setCalculatedDistanceTo(Double calculatedDistanceTo) {
		this.distanceToObjectLabel.setText(calculatedDistanceTo == null ? "--" : doubleFormatter.format(calculatedDistanceTo));
	}

	/**
	 * Method for disabling/enabling buttons which can be performed only on objects which are saved in DB
	 *
	 * @param isEnabled whether components will be available for edition
	 */
	public void setEnableControlComponents(boolean isEnabled) {
		calculateShapeInfoButton.setEnabled(isEnabled);
		calculateDistanceButton.setEnabled(isEnabled);
		showOnMapButton.setEnabled(isEnabled);
	}

	/**
	 * Initializes UI components
	 */
	private void initUI() {
		squareMetersLabel.setText("<html>m<sup>2</sup></html>");
		// disable because of NOT calculating not inserted models
		setEnableControlComponents(false);

		closestObjectsDistanceSpinner.setModel(new SpinnerNumberModel(1, 0, 500, 1));

		for (SpatialObjectModel model : controller.getSpatialObjects()) {
			spatialObjectsComboBox.addItem(model);
		}

		showOnMapButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.selectAllWithinDistance((Integer) closestObjectsDistanceSpinner.getValue());
			}
		});

		calculateDistanceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SpatialObjectModel obj = (SpatialObjectModel) spatialObjectsComboBox.getSelectedItem();
				controller.recalculateDistanceToObjectAction(obj);
			}
		});

		calculateShapeInfoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.recalculateShapeInfoAction();
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.detailCancelButtonAction();
			}
		});

		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.detailSaveButtonAction();
			}
		});

		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.detailDeleteButtonAction(spatialObject);
			}
		});
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
		rootPanel.setLayout(new GridLayoutManager(9, 5, new Insets(15, 15, 15, 15), -1, -1));
		rootPanel.setEnabled(true);
		final JLabel label1 = new JLabel();
		label1.setFont(new Font(label1.getFont().getName(), Font.BOLD, 20));
		label1.setText("Building detail");
		rootPanel.add(label1, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label2 = new JLabel();
		label2.setFont(new Font(label2.getFont().getName(), Font.BOLD, label2.getFont().getSize()));
		label2.setText("ID:");
		rootPanel.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label3 = new JLabel();
		label3.setFont(new Font(label3.getFont().getName(), Font.BOLD, label3.getFont().getSize()));
		label3.setText("Name:");
		rootPanel.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label4 = new JLabel();
		label4.setFont(new Font(label4.getFont().getName(), Font.BOLD, label4.getFont().getSize()));
		label4.setText("Type:");
		rootPanel.add(label4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		idTextField = new JTextField();
		idTextField.setEditable(false);
		rootPanel.add(idTextField, new GridConstraints(1, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		nameTextField = new JTextField();
		rootPanel.add(nameTextField, new GridConstraints(2, 1, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		typeComboBox = new JComboBox();
		typeComboBox.setEditable(false);
		typeComboBox.setEnabled(true);
		rootPanel.add(typeComboBox, new GridConstraints(3, 1, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		cancelButton = new JButton();
		cancelButton.setText("Cancel");
		rootPanel.add(cancelButton, new GridConstraints(5, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		deleteButton = new JButton();
		deleteButton.setText("Delete");
		rootPanel.add(deleteButton, new GridConstraints(5, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		saveButton = new JButton();
		saveButton.setText("Save");
		rootPanel.add(saveButton, new GridConstraints(5, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
		rootPanel.add(panel1, new GridConstraints(7, 1, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		panel1.setBorder(BorderFactory.createTitledBorder("Distance to other object"));
		final JLabel label5 = new JLabel();
		label5.setText("Select object");
		panel1.add(label5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label6 = new JLabel();
		label6.setText("Distance");
		panel1.add(label6, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JPanel panel2 = new JPanel();
		panel2.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		panel1.add(panel2, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		distanceToObjectLabel = new JLabel();
		distanceToObjectLabel.setText("--");
		panel2.add(distanceToObjectLabel);
		final JLabel label7 = new JLabel();
		label7.setText("m");
		panel2.add(label7);
		spatialObjectsComboBox = new JComboBox();
		panel1.add(spatialObjectsComboBox, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JPanel panel3 = new JPanel();
		panel3.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
		panel1.add(panel3, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		calculateDistanceButton = new JButton();
		calculateDistanceButton.setEnabled(false);
		calculateDistanceButton.setText("Calculate");
		panel3.add(calculateDistanceButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer1 = new Spacer();
		panel3.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JPanel panel4 = new JPanel();
		panel4.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
		panel4.setToolTipText("");
		rootPanel.add(panel4, new GridConstraints(6, 1, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		panel4.setBorder(BorderFactory.createTitledBorder("Informations about object"));
		areaWrapper = new JPanel();
		areaWrapper.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		areaWrapper.setFont(new Font(areaWrapper.getFont().getName(), Font.BOLD, areaWrapper.getFont().getSize()));
		panel4.add(areaWrapper, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		areaWrapper.setBorder(BorderFactory.createTitledBorder("Area"));
		final JPanel panel5 = new JPanel();
		panel5.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		areaWrapper.add(panel5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		shapeAreaLabel = new JLabel();
		shapeAreaLabel.setText("--");
		panel5.add(shapeAreaLabel);
		squareMetersLabel = new JLabel();
		squareMetersLabel.setText("m2");
		squareMetersLabel.putClientProperty("html.disable", Boolean.FALSE);
		panel5.add(squareMetersLabel);
		final JPanel panel6 = new JPanel();
		panel6.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		panel6.setFont(new Font(panel6.getFont().getName(), Font.BOLD, panel6.getFont().getSize()));
		panel4.add(panel6, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		panel6.setBorder(BorderFactory.createTitledBorder("Length"));
		final JPanel panel7 = new JPanel();
		panel7.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panel6.add(panel7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		shapeLengthLabel = new JLabel();
		shapeLengthLabel.setText("--");
		panel7.add(shapeLengthLabel);
		final JLabel label8 = new JLabel();
		label8.setText("m");
		panel7.add(label8);
		final JPanel panel8 = new JPanel();
		panel8.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
		panel4.add(panel8, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		calculateShapeInfoButton = new JButton();
		calculateShapeInfoButton.setEnabled(false);
		calculateShapeInfoButton.setText("Calculate");
		panel8.add(calculateShapeInfoButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer2 = new Spacer();
		panel8.add(spacer2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JPanel panel9 = new JPanel();
		panel9.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
		rootPanel.add(panel9, new GridConstraints(8, 1, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		panel9.setBorder(BorderFactory.createTitledBorder("Closest objects within distance"));
		final JLabel label9 = new JLabel();
		label9.setText("Distance");
		panel9.add(label9, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		closestObjectsDistanceSpinner = new JSpinner();
		closestObjectsDistanceSpinner.setEnabled(true);
		panel9.add(closestObjectsDistanceSpinner, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		showOnMapButton = new JButton();
		showOnMapButton.setEnabled(false);
		showOnMapButton.setText("Show on map");
		panel9.add(showOnMapButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer3 = new Spacer();
		panel9.add(spacer3, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JLabel label10 = new JLabel();
		label10.setEnabled(true);
		label10.setFont(new Font(label10.getFont().getName(), Font.BOLD, label10.getFont().getSize()));
		label10.setText("Z-Index");
		rootPanel.add(label10, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		zIndexSpinner = new JSpinner();
		rootPanel.add(zIndexSpinner, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer4 = new Spacer();
		rootPanel.add(spacer4, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return rootPanel;
	}
}
