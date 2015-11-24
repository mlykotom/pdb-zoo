package cz.vutbr.fit.pdb.ateam.gui.tabs.details;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import cz.vutbr.fit.pdb.ateam.controller.Controller;
import cz.vutbr.fit.pdb.ateam.controller.SpatialObjectTabController;
import cz.vutbr.fit.pdb.ateam.gui.BasePanel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectTypeModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by Jakub on 24.11.2015.
 */
public class SpatialObjectDetail extends BasePanel {
	private JPanel rootPanel;
	private JTextField idTextField;
	private JTextField nameTextField;
	private JComboBox<String> typeComboBox;
	private JButton saveButton;
	private JButton cancelButton;
	private JButton deleteButton;
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
		typeComboBox.setSelectedItem(spatialObject.getType().getName());
	}

	public String getNameTextFieldVallue() {
		return nameTextField.getText();
	}

	public String getTypeComboBoxVallue() {
		return (String) typeComboBox.getSelectedItem();
	}

	public void setTypeComboBoxModel(ArrayList<SpatialObjectTypeModel> objectTypes) {

		DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) typeComboBox.getModel();
		model.removeAllElements();

		for (SpatialObjectTypeModel type : objectTypes) {
			model.addElement(type.getName());
		}

		typeComboBox.setModel(model);
	}

	private void initUI() {

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.cancelButton();
			}
		});

		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.saveButton();
			}
		});

		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.spatialObjectsTableDeleteAction(spatialObject);
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
		rootPanel.setLayout(new GridLayoutManager(5, 6, new Insets(15, 15, 15, 15), -1, -1));
		rootPanel.setEnabled(true);
		final JLabel label1 = new JLabel();
		label1.setFont(new Font(label1.getFont().getName(), Font.BOLD, 20));
		label1.setText("Building detail");
		rootPanel.add(label1, new GridConstraints(0, 0, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label2 = new JLabel();
		label2.setFont(new Font(label2.getFont().getName(), Font.BOLD, label2.getFont().getSize()));
		label2.setText("ID:");
		rootPanel.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label3 = new JLabel();
		label3.setFont(new Font(label3.getFont().getName(), Font.BOLD, label3.getFont().getSize()));
		label3.setText("Name:");
		rootPanel.add(label3, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label4 = new JLabel();
		label4.setFont(new Font(label4.getFont().getName(), Font.BOLD, label4.getFont().getSize()));
		label4.setText("Type:");
		rootPanel.add(label4, new GridConstraints(3, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		idTextField = new JTextField();
		idTextField.setEditable(false);
		rootPanel.add(idTextField, new GridConstraints(1, 3, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		nameTextField = new JTextField();
		rootPanel.add(nameTextField, new GridConstraints(2, 3, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		typeComboBox = new JComboBox();
		typeComboBox.setEditable(false);
		typeComboBox.setEnabled(true);
		rootPanel.add(typeComboBox, new GridConstraints(3, 3, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		cancelButton = new JButton();
		cancelButton.setText("Cancel");
		rootPanel.add(cancelButton, new GridConstraints(4, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		deleteButton = new JButton();
		deleteButton.setText("Delete");
		rootPanel.add(deleteButton, new GridConstraints(4, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		saveButton = new JButton();
		saveButton.setText("Save");
		rootPanel.add(saveButton, new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return rootPanel;
	}
}
