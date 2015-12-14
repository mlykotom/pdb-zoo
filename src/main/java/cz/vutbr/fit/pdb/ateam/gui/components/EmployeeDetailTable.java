package cz.vutbr.fit.pdb.ateam.gui.components;

import cz.vutbr.fit.pdb.ateam.adapter.DataManager;
import cz.vutbr.fit.pdb.ateam.model.employee.EmployeeModel;
import cz.vutbr.fit.pdb.ateam.utils.Utils;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Tomas Hanus on 03.12.2015.
 */
public class EmployeeDetailTable extends JTable {
	private static final String FOREVER_DATE = "01-Jan-2500";

	private EmployeeDetailTableModel tableModel;

	public EmployeeDetailTable() {
		super();

		tableModel = new EmployeeDetailTableModel();
		setModel(tableModel);

		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tableModel);
		sorter.setComparator(0, new Comparator<Long>() {
			@Override
			public int compare(Long o1, Long o2) {
				return o1.compareTo(o2);
			}
		});
		this.setRowSorter(sorter);
	}

	public void addEmployeeModel(EmployeeModel employeeModel) {
		tableModel.addEmployeeModel(employeeModel);
	}

	/**
	 * Sets columns widths according size of displayed data.
	 */
	public void setColumnsWidth() {
		for (int i = 0; i <= 3; i++) {
			switch (i) {
				case 0:
					getColumnModel().getColumn(i).setPreferredWidth(35);
					getColumnModel().getColumn(i).setMaxWidth(35);
					break;
				case 1:
				case 2:
					getColumnModel().getColumn(i).setPreferredWidth(80);
					getColumnModel().getColumn(i).setMaxWidth(80);
					break;
				case 3:
					getColumnModel().getColumn(i).setPreferredWidth(80);
					break;
				default:
					getColumnModel().getColumn(i).setPreferredWidth(80);
			}
		}
	}

	private class EmployeeDetailTableModel extends AbstractTableModel {
		private String[] columnNames;
		private ArrayList<EmployeeModel> objectsList;

		public EmployeeDetailTableModel() {
			columnNames = new String[]{"ID", "FROM", "TO", "LOCATION"};
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
			EmployeeModel employeeModel = objectsList.get(rowIndex);

			if (employeeModel == null) return "";

			switch (columnIndex) {
				case 0:
					return employeeModel.getShiftID();
				case 1:
					return employeeModel.getDateFrom();
				case 2:
					if (employeeModel.getDateTo() != null)
						return getStringDate(employeeModel.getDateTo());
				case 3:
					if (DataManager.getInstance().getSpatialObjectModelWithID(employeeModel.getLocation()) != null)
						return DataManager.getInstance().getSpatialObjectModelWithID(employeeModel.getLocation()).toString();
				default:
					return "";
			}
		}

		private String getStringDate(Date dateTo) {
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
			Date foreverDate = dateTo;
			try {
				foreverDate = formatter.parse(FOREVER_DATE);

			} catch (ParseException e) {
				e.printStackTrace();
			}
			if (Utils.removeTime(dateTo).equals(foreverDate)) {
				return "now";
			}
			return dateTo.toString();
		}

		@Override
		public String getColumnName(int columnIndex) {
			if (columnIndex >= columnNames.length)
				return "";

			return columnNames[columnIndex];
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == 4 || columnIndex == 5;
		}
	}
}
