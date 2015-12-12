package cz.vutbr.fit.pdb.ateam.gui.tabs.details;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import cz.vutbr.fit.pdb.ateam.controller.Controller;
import cz.vutbr.fit.pdb.ateam.controller.EmployeesTabController;
import cz.vutbr.fit.pdb.ateam.gui.BasePanel;
import cz.vutbr.fit.pdb.ateam.gui.tabs.EmployeesTab;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.utils.DateLabelFormatter;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Date;
import java.util.Properties;

/**
 * Created by Tomas on 12/6/2015.
 */
public class EmployeeShiftEditPanel extends BasePanel {

	private final EmployeesTabController controller;
	private EmployeesTab tab;
	private ArrayList<SpatialObjectModel> locations;
	private JPanel rootPanel;
	private JRadioButton editRadioButton;
	private JRadioButton deleteRadioButton;
	private JPanel shiftFromDatePickerPanel;
	private JPanel shiftToDatePickerPanel;
	private JDatePickerImpl shiftFromDatePicker;
	private JDatePickerImpl shiftToDatePicker;
	private JButton confirmButton;
	private JComboBox shiftLocationComboBox;
	private JButton discardButton;
	private JLabel nameLabel;
	private JPanel employeeTitlePane;
	private JLabel surnameLabel;
	private Date dateFrom;

	public EmployeeShiftEditPanel(EmployeesTab employeesTab, ArrayList<SpatialObjectModel> locations) {
		this.tab = employeesTab;
		this.locations = locations;
		this.controller = (EmployeesTabController) employeesTab.getController();
		add(rootPanel);

		initUI();
	}


	private void initUI() {
		this.nameLabel.setText(controller.getSelectedEmployeeModel().getName());
		this.surnameLabel.setText(controller.getSelectedEmployeeModel().getSurname());

		editRadioButton.setSelected(true);
		ButtonGroup bg = new ButtonGroup();
		bg.add(editRadioButton);
		bg.add(deleteRadioButton);

		this.shiftFromDatePicker = addDatePicker();
		this.shiftToDatePicker = addDatePicker();


		this.shiftFromDatePickerPanel.removeAll();
		this.shiftFromDatePickerPanel.add(shiftFromDatePicker);

		this.shiftToDatePickerPanel.removeAll();
		this.shiftToDatePickerPanel.add(shiftToDatePicker);

		for (SpatialObjectModel location : locations) shiftLocationComboBox.addItem(location);

		this.confirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.confirmUpdateDeleteAction(editRadioButton.isSelected());
			}
		});
		this.discardButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.discardEditEmployeeShiftAction();
			}
		});
	}

	@Override
	public Controller getController() {
		return this.controller;
	}

	private JDatePickerImpl addDatePicker() {
		final UtilDateModel dateModel = new UtilDateModel();

		final JDatePanelImpl datePanel = new JDatePanelImpl(dateModel);

		JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

		JFormattedTextField textField = datePicker.getJFormattedTextField();
		textField.setFont(new Font("Segoe UI Semilight", Font.BOLD, 14));
		dateModel.setValue(Calendar.getInstance().getTime());
		return datePicker;
	}

	public Date getDateFrom() {
		return (Date) this.shiftFromDatePicker.getModel().getValue();
	}

	public Date getDateTo() {
		return (Date) this.shiftToDatePicker.getModel().getValue();
	}

	public Long getSelectedLocation() {
		return ((SpatialObjectModel) this.shiftLocationComboBox.getSelectedItem()).getId();
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
		rootPanel.setLayout(new GridLayoutManager(10, 6, new Insets(0, 0, 0, 0), -1, -1));
		rootPanel.setBorder(BorderFactory.createTitledBorder(""));
		final JLabel label1 = new JLabel();
		label1.setFont(new Font(label1.getFont().getName(), Font.BOLD, 18));
		label1.setText("Choose an action:");
		rootPanel.add(label1, new GridConstraints(3, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer1 = new Spacer();
		rootPanel.add(spacer1, new GridConstraints(3, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final Spacer spacer2 = new Spacer();
		rootPanel.add(spacer2, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final Spacer spacer3 = new Spacer();
		rootPanel.add(spacer3, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final Spacer spacer4 = new Spacer();
		rootPanel.add(spacer4, new GridConstraints(3, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final Spacer spacer5 = new Spacer();
		rootPanel.add(spacer5, new GridConstraints(9, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		editRadioButton = new JRadioButton();
		editRadioButton.setFont(new Font(editRadioButton.getFont().getName(), editRadioButton.getFont().getStyle(), 16));
		editRadioButton.setText("Edit");
		rootPanel.add(editRadioButton, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		deleteRadioButton = new JRadioButton();
		deleteRadioButton.setFont(new Font(deleteRadioButton.getFont().getName(), deleteRadioButton.getFont().getStyle(), 16));
		deleteRadioButton.setText("Delete");
		rootPanel.add(deleteRadioButton, new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label2 = new JLabel();
		label2.setFont(new Font(label2.getFont().getName(), Font.BOLD, 18));
		label2.setText("Choose interval:");
		rootPanel.add(label2, new GridConstraints(5, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label3 = new JLabel();
		label3.setText("From");
		rootPanel.add(label3, new GridConstraints(5, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label4 = new JLabel();
		label4.setText("To");
		rootPanel.add(label4, new GridConstraints(6, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		shiftFromDatePickerPanel = new JPanel();
		shiftFromDatePickerPanel.setLayout(new BorderLayout(0, 0));
		rootPanel.add(shiftFromDatePickerPanel, new GridConstraints(5, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		shiftToDatePickerPanel = new JPanel();
		shiftToDatePickerPanel.setLayout(new BorderLayout(0, 0));
		rootPanel.add(shiftToDatePickerPanel, new GridConstraints(6, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		final JLabel label5 = new JLabel();
		label5.setFont(new Font(label5.getFont().getName(), Font.BOLD, 18));
		label5.setText("Choose location:");
		rootPanel.add(label5, new GridConstraints(7, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		shiftLocationComboBox = new JComboBox();
		rootPanel.add(shiftLocationComboBox, new GridConstraints(7, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		discardButton = new JButton();
		discardButton.setText("Discard");
		rootPanel.add(discardButton, new GridConstraints(8, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		confirmButton = new JButton();
		confirmButton.setText("Confirm");
		rootPanel.add(confirmButton, new GridConstraints(8, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		employeeTitlePane = new JPanel();
		employeeTitlePane.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
		rootPanel.add(employeeTitlePane, new GridConstraints(1, 1, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		nameLabel = new JLabel();
		nameLabel.setFont(new Font(nameLabel.getFont().getName(), Font.BOLD, 36));
		nameLabel.setText("Janko");
		employeeTitlePane.add(nameLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		surnameLabel = new JLabel();
		surnameLabel.setFont(new Font(surnameLabel.getFont().getName(), surnameLabel.getFont().getStyle(), 34));
		surnameLabel.setText("Hrasko");
		employeeTitlePane.add(surnameLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer6 = new Spacer();
		employeeTitlePane.add(spacer6, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JSeparator separator1 = new JSeparator();
		rootPanel.add(separator1, new GridConstraints(2, 1, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return rootPanel;
	}
}
