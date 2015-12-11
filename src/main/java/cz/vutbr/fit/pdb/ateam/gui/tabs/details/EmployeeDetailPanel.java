package cz.vutbr.fit.pdb.ateam.gui.tabs.details;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import cz.vutbr.fit.pdb.ateam.controller.Controller;
import cz.vutbr.fit.pdb.ateam.controller.EmployeesTabController;
import cz.vutbr.fit.pdb.ateam.gui.BasePanel;
import cz.vutbr.fit.pdb.ateam.gui.tabs.EmployeesTab;
import cz.vutbr.fit.pdb.ateam.model.employee.EmployeeModel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created by Tomas on 10/24/2015.
 */
public class EmployeeDetailPanel extends BasePanel {

	private static final String[] EMPLOYEE_DETAIL_HEADER = {"New Employee", "Edit Employee"};

	public static final int NEW_EMPLOYEE = 0;
	public static final int EDIT_EMPLOYEE = 1;

	private JTable employeeDetailTable;
	private JPanel rootPanel;
	private final EmployeesTabController controller;
	private JTextField nameEditField;
	private JTextField surnameEditField;
	private JButton saveButton;
	private JButton discardButton;
	private JComboBox<SpatialObjectModel> locationComboBox;
	private JTextField idField;
	private JLabel employeeDetailHeader;
	private JCheckBox showHistoryCheckBox;
	private JPanel tablePanel;
	private JButton editButton;
	private JPanel historyPane;
	private EmployeesTab tab;
	private int employeeDetailPanelMode;
	private Long locationComboBoxValue;

	public EmployeeDetailPanel(EmployeesTab panel) {
		this.tab = panel;
		this.controller = (EmployeesTabController) tab.getController();
		add(rootPanel);
		initUI();
	}

	public void setEmployeeDetailTable(JTable table) {
		employeeDetailTable = table;
		tablePanel.removeAll();

		JScrollPane jScrollPane = new JScrollPane(employeeDetailTable);
		tablePanel.add(jScrollPane);
	}

	private void initUI() {
		this.idField.setEnabled(false);
//		hideEmployeeDetailTable();
		discardButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.discardUserAction();
			}
		});
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.saveEmployee(employeeDetailPanelMode);
			}
		});
		showHistoryCheckBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				controller.showHistoryAction(showHistoryCheckBox.isSelected());
			}
		});
		editButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.editShiftAction();
			}
		});
	}

	/**
	 * Prepopulate EmployeeDetailPanel with autogenerated ID for new Employee or with existing
	 * employee's personal information.
	 *
	 * @param employee       new/existing employee
	 * @param spatialObjects
	 * @param editMode       NEW_EMPLOYEE or EDIT_EMPLOYEE to set Panel Mode
	 */
	public void populateEmployeeDetailPanel(EmployeeModel employee, java.util.List<SpatialObjectModel> spatialObjects, int editMode) {
		this.employeeDetailPanelMode = editMode;
		this.employeeDetailHeader.setText(EMPLOYEE_DETAIL_HEADER[this.employeeDetailPanelMode]);
		this.idField.setText(employee.getId().toString());
		this.nameEditField.setText(employee.getName());
		this.surnameEditField.setText(employee.getSurname());

		if (editMode == NEW_EMPLOYEE) {
			showHistoryCheckBox.setVisible(false);
		}
		hideHistoryShiftPane();

		for (SpatialObjectModel spatialObject : spatialObjects) locationComboBox.addItem(spatialObject);
	}

	public void hideHistoryShiftPane() {
		historyPane.setVisible(false);
	}

	public void showHistoryShiftPane() {
		historyPane.setVisible(true);
	}

	public JComboBox getLocationComboBox() {
		return locationComboBox;
	}

	@Override
	public Controller getController() {
		return tab.getController();
	}

	private void createUIComponents() {
		// TODO: place custom component creation code here
	}

//	public void hideEmployeeDetailTable() {
//		this.tablePanel.setVisible(false);
//	}

//	public void showEmployeeDetailTable() {
//		this.tablePanel.setVisible(true);
//	}

	public String getNameTextFieldValue() {
		return nameEditField.getText();
	}

	public String getSurnameTextFieldValue() {
		return surnameEditField.getText();
	}

	public Long getLocationComboBoxValue() {
		return ((SpatialObjectModel) locationComboBox.getSelectedItem()).getId();
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
		rootPanel.setLayout(new GridLayoutManager(16, 5, new Insets(0, 0, 0, 0), -1, -1));
		final JLabel label1 = new JLabel();
		label1.setText("Name");
		rootPanel.add(label1, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(60, 21), null, 0, false));
		nameEditField = new JTextField();
		rootPanel.add(nameEditField, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final JLabel label2 = new JLabel();
		label2.setText("Surname");
		rootPanel.add(label2, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(60, 21), null, 0, false));
		surnameEditField = new JTextField();
		rootPanel.add(surnameEditField, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final Spacer spacer1 = new Spacer();
		rootPanel.add(spacer1, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final Spacer spacer2 = new Spacer();
		rootPanel.add(spacer2, new GridConstraints(11, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final Spacer spacer3 = new Spacer();
		rootPanel.add(spacer3, new GridConstraints(9, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		locationComboBox = new JComboBox();
		rootPanel.add(locationComboBox, new GridConstraints(7, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer4 = new Spacer();
		rootPanel.add(spacer4, new GridConstraints(10, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
		rootPanel.add(panel1, new GridConstraints(10, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		saveButton = new JButton();
		saveButton.setText("Save");
		panel1.add(saveButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		discardButton = new JButton();
		discardButton.setText("Discard");
		panel1.add(discardButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer5 = new Spacer();
		panel1.add(spacer5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JLabel label3 = new JLabel();
		label3.setText("Location");
		rootPanel.add(label3, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(60, 21), null, 0, false));
		final Spacer spacer6 = new Spacer();
		rootPanel.add(spacer6, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final Spacer spacer7 = new Spacer();
		rootPanel.add(spacer7, new GridConstraints(5, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JLabel label4 = new JLabel();
		label4.setText("ID");
		rootPanel.add(label4, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(60, 21), null, 0, false));
		idField = new JTextField();
		idField.setText("");
		rootPanel.add(idField, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		employeeDetailHeader = new JLabel();
		employeeDetailHeader.setFont(new Font(employeeDetailHeader.getFont().getName(), Font.BOLD, 26));
		employeeDetailHeader.setText("someText");
		rootPanel.add(employeeDetailHeader, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer8 = new Spacer();
		rootPanel.add(spacer8, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 50), null, null, 0, false));
		final Spacer spacer9 = new Spacer();
		rootPanel.add(spacer9, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(60, 14), null, 0, false));
		final Spacer spacer10 = new Spacer();
		rootPanel.add(spacer10, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		showHistoryCheckBox = new JCheckBox();
		showHistoryCheckBox.setFont(new Font(showHistoryCheckBox.getFont().getName(), Font.BOLD, 18));
		showHistoryCheckBox.setText("Show History");
		rootPanel.add(showHistoryCheckBox, new GridConstraints(11, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer11 = new Spacer();
		rootPanel.add(spacer11, new GridConstraints(8, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final Spacer spacer12 = new Spacer();
		rootPanel.add(spacer12, new GridConstraints(15, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final JSeparator separator1 = new JSeparator();
		rootPanel.add(separator1, new GridConstraints(12, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		historyPane = new JPanel();
		historyPane.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
		rootPanel.add(historyPane, new GridConstraints(13, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		editButton = new JButton();
		editButton.setEnabled(true);
		editButton.setText("Edit");
		historyPane.add(editButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer13 = new Spacer();
		historyPane.add(spacer13, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final Spacer spacer14 = new Spacer();
		historyPane.add(spacer14, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		tablePanel = new JPanel();
		tablePanel.setLayout(new BorderLayout(0, 0));
		tablePanel.setBackground(new Color(-16432887));
		historyPane.add(tablePanel, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(150, 150), null, new Dimension(-1, 300), 0, false));
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return rootPanel;
	}
}