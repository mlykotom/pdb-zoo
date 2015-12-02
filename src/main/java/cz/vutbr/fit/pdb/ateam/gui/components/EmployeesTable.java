package cz.vutbr.fit.pdb.ateam.gui.components;

import cz.vutbr.fit.pdb.ateam.controller.EmployeesTableController;
import cz.vutbr.fit.pdb.ateam.model.EmployeeModel;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Tomas Hanus on 28.11.2015.
 */
public class EmployeesTable extends JTable {
	private EmployeesTableModel tableModel;

	public EmployeesTable (EmployeesTableController controller) {
		super();

		tableModel = new EmployeesTableModel();

		setModel(tableModel);

		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tableModel);
		sorter.setComparator(0, new Comparator<Long>() {
			@Override
			public int compare(Long o1, Long o2) {
				return o1.compareTo(o2);
			}
		});
		this.setRowSorter(sorter);

		getColumn("EDIT").setCellRenderer(new ButtonRenderer());
		getColumn("EDIT").setCellEditor(
				new ButtonEditor(controller));
	}

	public void addEmployeeModel(EmployeeModel employeeModel) {
		tableModel.addEmployeeModel(employeeModel);
	}

	private class EmployeesTableModel extends AbstractTableModel {
		private String[] columnNames;
		private ArrayList<EmployeeModel> objectsList;

		public EmployeesTableModel() {
			columnNames = new String[] {"ID", "NAME", "SURNAME", "LOCATION", "EDIT"};
			objectsList = new ArrayList<>();
		}

		public void addEmployeeModel(EmployeeModel EmployeeModel) {
			objectsList.add(EmployeeModel);
			fireTableRowsInserted(objectsList.size() - 1, objectsList.size() - 1);
		}


		public EmployeeModel getEmployeeModel(int rowIndex) {
			return objectsList.get(rowIndex);
		}

		@Override
		public int getRowCount() {
			return objectsList.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			EmployeeModel EmployeeModel = objectsList.get(rowIndex);

			switch (columnIndex) {
				case 0:
					return EmployeeModel.getId();
				case 1:
					return EmployeeModel.getName();
				case 2:
					return EmployeeModel.getSurname();
				case 3:
					return EmployeeModel.getLocation();
				case 4:
					return "Edit";
				default:
					return "";
			}
		}

		@Override
		public String getColumnName(int columnIndex) {
			if(columnIndex >= columnNames.length)
				return "";

			return columnNames[columnIndex];
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == 4 || columnIndex == 5;
		}
	}

	private class ButtonRenderer extends JButton implements TableCellRenderer {

		public ButtonRenderer() {
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
		                                               boolean isSelected, boolean hasFocus, int row, int column) {
			if (isSelected) {
				setForeground(table.getSelectionForeground());
				setBackground(table.getSelectionBackground());
			} else {
				setForeground(table.getForeground());
				setBackground(UIManager.getColor("Button.background"));
			}
			setText((value == null) ? "" : value.toString());
			return this;
		}
	}

	private class ButtonEditor extends DefaultCellEditor {
		protected JButton button;

		private String label;

		private EmployeesTableController controller;
		private EmployeeModel EmployeeModel;
		private String pushedButton;

		private boolean isPushed;

		public ButtonEditor(EmployeesTableController controller) {
			super(new JCheckBox());

			this.controller = controller;
			button = new JButton();
			button.setOpaque(true);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					fireEditingStopped();
				}
			});
		}

		public Component getTableCellEditorComponent(JTable table, Object value,
		                                             boolean isSelected, int row, int column) {
			if (isSelected) {
				button.setForeground(table.getSelectionForeground());
				button.setBackground(table.getSelectionBackground());
			} else {
				button.setForeground(table.getForeground());
				button.setBackground(table.getBackground());
			}
			label = (value == null) ? "" : value.toString();
			button.setText(label);
			isPushed = true;

			this.EmployeeModel = ((EmployeesTableModel)table.getModel()).getEmployeeModel(row);
			if(column == 4) this.pushedButton = "edit";

			return button;
		}

		public Object getCellEditorValue() {
			if (isPushed) {
				if(pushedButton.equals("edit")) controller.EmployeesTableEditAction(EmployeeModel);
			}
			isPushed = false;
			return label;
		}

		public boolean stopCellEditing() {
			isPushed = false;
			return super.stopCellEditing();
		}

		protected void fireEditingStopped() {
			super.fireEditingStopped();
		}
	}
}
