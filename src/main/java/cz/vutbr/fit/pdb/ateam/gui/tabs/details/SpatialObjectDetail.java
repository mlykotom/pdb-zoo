package cz.vutbr.fit.pdb.ateam.gui.tabs.details;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import cz.vutbr.fit.pdb.ateam.controller.Controller;
import cz.vutbr.fit.pdb.ateam.controller.SpatialObjectTabController;
import cz.vutbr.fit.pdb.ateam.gui.BasePanel;
import cz.vutbr.fit.pdb.ateam.gui.components.SpatialDetailTable;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectTypeModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel of the detailed info about spatial object.
 *
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
	private JSpinner closestNSpinner;
	private JPanel spatialNObectsTablePanel;
	private JButton calculateNObjectsButton;
	private JCheckBox sameTypeCheckBox;
	private SpatialObjectTabController controller;
	private SpatialObjectModel spatialObject;

	private SpatialDetailTable spatialDetailTable = new SpatialDetailTable();

	public SpatialDetailTable getSpatialDetailTable() {
		return spatialDetailTable;
	}

	public SpatialObjectDetail(SpatialObjectTabController controller) {
		this.controller = controller;
		add(rootPanel);
		initUI();
	}

	/**
	 * Prepares right tab for viewing spatial object
	 *
	 * @param spatialObject this spatial object will be selected in detail
	 */
	public void setSpatialObject(SpatialObjectModel spatialObject) {
		this.spatialObject = spatialObject;

		idTextField.setText(spatialObject.getId().toString());
		nameTextField.setText(spatialObject.getName());
		zIndexSpinner.setValue(spatialObject.getzIndex());
		typeComboBox.setSelectedItem(spatialObject.getType());

		setEnableControlComponents(!spatialObject.isNew());
		setCalculatedInfo(null, null);
		setCalculatedDistanceTo(null);
		getSpatialDetailTable().clearModels();
	}

	public String getNameTextFieldValue() {
		return nameTextField.getText();
	}

	public Integer getZIndexSpinnerValue() {
		return (Integer) zIndexSpinner.getValue();
	}

	/**
	 * Getter for spatial type comboxox's value
	 *
	 * @return spatial object type model
	 */
	public SpatialObjectTypeModel getTypeComboBoxVallue() {
		return (SpatialObjectTypeModel) typeComboBox.getSelectedItem();
	}

	/**
	 * Sets model for combobox (spatial types)
	 *
	 * @param objectTypes list of spatial object types
	 */
	public void setTypeComboBoxModel(List<SpatialObjectTypeModel> objectTypes) {
		typeComboBox.removeAllItems();
		for (SpatialObjectTypeModel type : objectTypes) {
			typeComboBox.addItem(type);
		}
	}

	/**
	 * Set calculated informations and enable state for button
	 *
	 * @param area   object's area
	 * @param length object's length
	 */
	public void setCalculatedInfo(Double area, Double length) {
		shapeAreaLabel.setText(area == null ? "--" : doubleFormatter.format(area));
		shapeLengthLabel.setText(length == null ? "--" : doubleFormatter.format(length));
	}

	/**
	 * Setter for calculated distance to specified object from combo box
	 *
	 * @param calculatedDistanceTo actual value or null when should be set like not calculated
	 */
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
		calculateNObjectsButton.setEnabled(isEnabled);
		sameTypeCheckBox.setSelected(false);
	}

	/**
	 * Initializes UI components
	 */
	private void initUI() {
		squareMetersLabel.setText("<html>m<sup>2</sup></html>");
		// disable because of NOT calculating not inserted models
		setEnableControlComponents(false);

		closestObjectsDistanceSpinner.setModel(new SpinnerNumberModel(1, 0, 500, 1));
		closestNSpinner.setModel(new SpinnerNumberModel(1, 1, 500, 1));

		for (SpatialObjectModel model : controller.getSpatialObjects()) {
			spatialObjectsComboBox.addItem(model);
		}

		spatialNObectsTablePanel.add(new JScrollPane(spatialDetailTable));

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

		calculateNObjectsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.selectClosestN((Integer) closestNSpinner.getValue(), sameTypeCheckBox.isSelected());
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
		rootPanel.setLayout(new GridLayoutManager(6, 3, new Insets(15, 15, 15, 15), -1, -1));
		rootPanel.setEnabled(true);
		final JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayoutManager(5, 4, new Insets(0, 0, 0, 0), -1, -1));
		rootPanel.add(panel1, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		deleteButton = new JButton();
		deleteButton.setText("Delete");
		panel1.add(deleteButton, new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		zIndexSpinner = new JSpinner();
		panel1.add(zIndexSpinner, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label1 = new JLabel();
		label1.setEnabled(true);
		label1.setFont(new Font(label1.getFont().getName(), Font.BOLD, label1.getFont().getSize()));
		label1.setText("Z-Index");
		panel1.add(label1, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label2 = new JLabel();
		label2.setFont(new Font(label2.getFont().getName(), Font.BOLD, label2.getFont().getSize()));
		label2.setText("Type:");
		panel1.add(label2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label3 = new JLabel();
		label3.setFont(new Font(label3.getFont().getName(), Font.BOLD, label3.getFont().getSize()));
		label3.setText("Name:");
		panel1.add(label3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		typeComboBox = new JComboBox();
		typeComboBox.setEditable(false);
		typeComboBox.setEnabled(true);
		panel1.add(typeComboBox, new GridConstraints(2, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		nameTextField = new JTextField();
		panel1.add(nameTextField, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		idTextField = new JTextField();
		idTextField.setEditable(false);
		panel1.add(idTextField, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final JLabel label4 = new JLabel();
		label4.setFont(new Font(label4.getFont().getName(), Font.BOLD, label4.getFont().getSize()));
		label4.setText("ID:");
		panel1.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		saveButton = new JButton();
		saveButton.setText("Save");
		panel1.add(saveButton, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
		panel2.setToolTipText("");
		rootPanel.add(panel2, new GridConstraints(2, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		panel2.setBorder(BorderFactory.createTitledBorder("Informations about object"));
		areaWrapper = new JPanel();
		areaWrapper.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		areaWrapper.setFont(new Font(areaWrapper.getFont().getName(), Font.BOLD, areaWrapper.getFont().getSize()));
		panel2.add(areaWrapper, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		areaWrapper.setBorder(BorderFactory.createTitledBorder("Area"));
		final JPanel panel3 = new JPanel();
		panel3.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		areaWrapper.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		shapeAreaLabel = new JLabel();
		shapeAreaLabel.setText("--");
		panel3.add(shapeAreaLabel);
		squareMetersLabel = new JLabel();
		squareMetersLabel.setText("m2");
		squareMetersLabel.putClientProperty("html.disable", Boolean.FALSE);
		panel3.add(squareMetersLabel);
		final JPanel panel4 = new JPanel();
		panel4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		panel4.setFont(new Font(panel4.getFont().getName(), Font.BOLD, panel4.getFont().getSize()));
		panel2.add(panel4, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		panel4.setBorder(BorderFactory.createTitledBorder("Length"));
		final JPanel panel5 = new JPanel();
		panel5.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panel4.add(panel5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		shapeLengthLabel = new JLabel();
		shapeLengthLabel.setText("--");
		panel5.add(shapeLengthLabel);
		final JLabel label5 = new JLabel();
		label5.setText("m");
		panel5.add(label5);
		final JPanel panel6 = new JPanel();
		panel6.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
		panel2.add(panel6, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		calculateShapeInfoButton = new JButton();
		calculateShapeInfoButton.setEnabled(false);
		calculateShapeInfoButton.setText("Calculate");
		panel6.add(calculateShapeInfoButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer1 = new Spacer();
		panel6.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JPanel panel7 = new JPanel();
		panel7.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
		rootPanel.add(panel7, new GridConstraints(3, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		panel7.setBorder(BorderFactory.createTitledBorder("Distance to other object"));
		final JLabel label6 = new JLabel();
		label6.setText("Select object");
		panel7.add(label6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label7 = new JLabel();
		label7.setText("Distance");
		panel7.add(label7, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JPanel panel8 = new JPanel();
		panel8.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		panel7.add(panel8, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		distanceToObjectLabel = new JLabel();
		distanceToObjectLabel.setText("--");
		panel8.add(distanceToObjectLabel);
		final JLabel label8 = new JLabel();
		label8.setText("m");
		panel8.add(label8);
		spatialObjectsComboBox = new JComboBox();
		panel7.add(spatialObjectsComboBox, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JPanel panel9 = new JPanel();
		panel9.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
		panel7.add(panel9, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		calculateDistanceButton = new JButton();
		calculateDistanceButton.setEnabled(false);
		calculateDistanceButton.setText("Calculate");
		panel9.add(calculateDistanceButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer2 = new Spacer();
		panel9.add(spacer2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JPanel panel10 = new JPanel();
		panel10.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
		rootPanel.add(panel10, new GridConstraints(4, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		panel10.setBorder(BorderFactory.createTitledBorder("Closest objects within distance"));
		final JLabel label9 = new JLabel();
		label9.setText("Distance");
		panel10.add(label9, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		closestObjectsDistanceSpinner = new JSpinner();
		closestObjectsDistanceSpinner.setEnabled(true);
		panel10.add(closestObjectsDistanceSpinner, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		showOnMapButton = new JButton();
		showOnMapButton.setEnabled(false);
		showOnMapButton.setText("Show on map");
		panel10.add(showOnMapButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer3 = new Spacer();
		panel10.add(spacer3, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JPanel panel11 = new JPanel();
		panel11.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
		rootPanel.add(panel11, new GridConstraints(5, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		panel11.setBorder(BorderFactory.createTitledBorder("Closest N objects"));
		final JPanel panel12 = new JPanel();
		panel12.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
		panel11.add(panel12, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		final JLabel label10 = new JLabel();
		label10.setText("N (objects count)");
		panel12.add(label10, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		closestNSpinner = new JSpinner();
		panel12.add(closestNSpinner, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		calculateNObjectsButton = new JButton();
		calculateNObjectsButton.setEnabled(false);
		calculateNObjectsButton.setText("Calculate");
		panel12.add(calculateNObjectsButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer4 = new Spacer();
		panel12.add(spacer4, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		sameTypeCheckBox = new JCheckBox();
		sameTypeCheckBox.setText("same type");
		panel12.add(sameTypeCheckBox, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		spatialNObectsTablePanel = new JPanel();
		spatialNObectsTablePanel.setLayout(new BorderLayout(0, 0));
		panel11.add(spatialNObectsTablePanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		final JLabel label11 = new JLabel();
		label11.setFont(new Font(label11.getFont().getName(), Font.BOLD, 20));
		label11.setText("Building detail");
		rootPanel.add(label11, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		cancelButton = new JButton();
		cancelButton.setText("Discard");
		rootPanel.add(cancelButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer5 = new Spacer();
		rootPanel.add(spacer5, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return rootPanel;
	}
}
