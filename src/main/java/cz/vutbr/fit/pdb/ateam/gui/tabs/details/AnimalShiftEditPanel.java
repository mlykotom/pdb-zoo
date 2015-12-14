package cz.vutbr.fit.pdb.ateam.gui.tabs.details;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import cz.vutbr.fit.pdb.ateam.controller.AnimalsTabController;
import cz.vutbr.fit.pdb.ateam.controller.Controller;
import cz.vutbr.fit.pdb.ateam.gui.BasePanel;
import cz.vutbr.fit.pdb.ateam.gui.components.validations.FloatBiggerOrEqualToZeroInputVerifier;
import cz.vutbr.fit.pdb.ateam.gui.tabs.AnimalsTab;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.utils.DateLabelFormatter;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author Tomas Hanus on 12/12/2015.
 */
public class AnimalShiftEditPanel extends BasePanel {

	private final AnimalsTabController controller;
	private AnimalsTab tab;
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
	private JPanel animalTitlePane;
	private JLabel surnameLabel;
	private JLabel shiftLocationLabel;
	private JLabel shiftWeightLabel;
	private JTextField shiftEditPanelWeightTextField;

	public AnimalShiftEditPanel(AnimalsTab animalsTab, ArrayList<SpatialObjectModel> locations) {
		this.tab = animalsTab;
		this.locations = locations;
		this.controller = (AnimalsTabController) animalsTab.getController();
		add(rootPanel);

		initUI();
	}

	/**
	 * Inits AnimalShiftEditPanel.
	 */
	private void initUI() {
		this.nameLabel.setText(controller.getSelectedAnimalModel().getName());
		this.surnameLabel.setText(controller.getSelectedAnimalModel().getSpecies());

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

		setInputValidators();

		setListeners();
	}

	/**
	 * In this method all input validators should be set.
	 */
	private void setInputValidators() {
		this.shiftEditPanelWeightTextField.setInputVerifier(new FloatBiggerOrEqualToZeroInputVerifier());
	}

	/**
	 * In this method all listeners should be set.
	 */
	private void setListeners() {
		editRadioButton.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				controller.switchBetweenEditAndDeleteAction(editRadioButton.isSelected());
			}
		});

		this.confirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.confirmUpdateDeleteAction(editRadioButton.isSelected());
			}
		});

		this.discardButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.discardEditAnimalShiftAction();
			}
		});
	}

	@Override
	public Controller getController() {
		return this.controller;
	}

	/**
	 * Method create new DatePicker for date Selection.
	 * @return newly created JDatePicker
	 */
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

	/**
	 * Set ShiftEdit Panel to editMode, so it displays controls needed to set location and weight
	 */
	public void setShiftEditPanelToEdit() {
		this.shiftLocationComboBox.setVisible(true);
		this.shiftLocationLabel.setVisible(true);
		this.shiftEditPanelWeightTextField.setVisible(true);
		this.shiftWeightLabel.setVisible(true);
	}

	/**
	 * Set ShiftEdit Panel to deleteMode, so it hides controls needed for setting location and weight.
	 */
	public void setShiftEditPanelToDelete() {
		this.shiftLocationComboBox.setVisible(false);
		this.shiftLocationLabel.setVisible(false);
		this.shiftEditPanelWeightTextField.setVisible(false);
		this.shiftWeightLabel.setVisible(false);
	}

	public Float getWeightTextField() {
		return Float.valueOf(this.shiftEditPanelWeightTextField.getText());
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
		rootPanel.setLayout(new GridLayoutManager(12, 8, new Insets(0, 0, 0, 0), -1, -1));
		rootPanel.setBorder(BorderFactory.createTitledBorder(""));
		final JLabel label1 = new JLabel();
		label1.setFont(new Font(label1.getFont().getName(), Font.BOLD, 18));
		label1.setText("Choose an action:");
		rootPanel.add(label1, new GridConstraints(3, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer1 = new Spacer();
		rootPanel.add(spacer1, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final Spacer spacer2 = new Spacer();
		rootPanel.add(spacer2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final Spacer spacer3 = new Spacer();
		rootPanel.add(spacer3, new GridConstraints(3, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final Spacer spacer4 = new Spacer();
		rootPanel.add(spacer4, new GridConstraints(11, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		editRadioButton = new JRadioButton();
		editRadioButton.setFont(new Font(editRadioButton.getFont().getName(), editRadioButton.getFont().getStyle(), 14));
		editRadioButton.setText("Edit");
		rootPanel.add(editRadioButton, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		deleteRadioButton = new JRadioButton();
		deleteRadioButton.setFont(new Font(deleteRadioButton.getFont().getName(), deleteRadioButton.getFont().getStyle(), 14));
		deleteRadioButton.setText("Delete");
		rootPanel.add(deleteRadioButton, new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label2 = new JLabel();
		label2.setFont(new Font(label2.getFont().getName(), Font.BOLD, 18));
		label2.setText("Choose interval:");
		rootPanel.add(label2, new GridConstraints(5, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label3 = new JLabel();
		label3.setFont(new Font(label3.getFont().getName(), label3.getFont().getStyle(), 14));
		label3.setText("From");
		rootPanel.add(label3, new GridConstraints(5, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label4 = new JLabel();
		label4.setFont(new Font(label4.getFont().getName(), label4.getFont().getStyle(), 14));
		label4.setText("To");
		rootPanel.add(label4, new GridConstraints(6, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		shiftLocationLabel = new JLabel();
		shiftLocationLabel.setFont(new Font(shiftLocationLabel.getFont().getName(), Font.BOLD, 18));
		shiftLocationLabel.setText("Choose location:");
		rootPanel.add(shiftLocationLabel, new GridConstraints(7, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		shiftLocationComboBox = new JComboBox();
		rootPanel.add(shiftLocationComboBox, new GridConstraints(7, 3, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		discardButton = new JButton();
		discardButton.setText("Discard");
		rootPanel.add(discardButton, new GridConstraints(10, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, new Dimension(80, -1), 0, false));
		animalTitlePane = new JPanel();
		animalTitlePane.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
		rootPanel.add(animalTitlePane, new GridConstraints(1, 1, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		nameLabel = new JLabel();
		nameLabel.setFont(new Font(nameLabel.getFont().getName(), Font.BOLD, 36));
		nameLabel.setText("Janko");
		animalTitlePane.add(nameLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		surnameLabel = new JLabel();
		surnameLabel.setFont(new Font(surnameLabel.getFont().getName(), surnameLabel.getFont().getStyle(), 34));
		surnameLabel.setText("Hrasko");
		animalTitlePane.add(surnameLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer5 = new Spacer();
		animalTitlePane.add(spacer5, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JSeparator separator1 = new JSeparator();
		rootPanel.add(separator1, new GridConstraints(2, 1, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		shiftToDatePickerPanel = new JPanel();
		shiftToDatePickerPanel.setLayout(new BorderLayout(0, 0));
		rootPanel.add(shiftToDatePickerPanel, new GridConstraints(6, 4, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, new Dimension(150, -1), 0, false));
		shiftFromDatePickerPanel = new JPanel();
		shiftFromDatePickerPanel.setLayout(new BorderLayout(0, 0));
		rootPanel.add(shiftFromDatePickerPanel, new GridConstraints(5, 4, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, new Dimension(150, -1), 0, false));
		confirmButton = new JButton();
		confirmButton.setText("Confirm");
		rootPanel.add(confirmButton, new GridConstraints(10, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, new Dimension(80, -1), 0, false));
		final Spacer spacer6 = new Spacer();
		rootPanel.add(spacer6, new GridConstraints(10, 5, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final Spacer spacer7 = new Spacer();
		rootPanel.add(spacer7, new GridConstraints(3, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JSeparator separator2 = new JSeparator();
		rootPanel.add(separator2, new GridConstraints(9, 1, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		shiftWeightLabel = new JLabel();
		shiftWeightLabel.setFont(new Font(shiftWeightLabel.getFont().getName(), Font.BOLD, 18));
		shiftWeightLabel.setText("Choose weight:");
		rootPanel.add(shiftWeightLabel, new GridConstraints(8, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		shiftEditPanelWeightTextField = new JTextField();
		shiftEditPanelWeightTextField.setText("");
		rootPanel.add(shiftEditPanelWeightTextField, new GridConstraints(8, 3, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return rootPanel;
	}
}
