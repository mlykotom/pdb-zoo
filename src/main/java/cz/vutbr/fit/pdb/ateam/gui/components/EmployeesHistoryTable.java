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
 * Created by Tomas on 12/13/2015.
 */
public class EmployeesHistoryTable extends JTable {
	private static final String FOREVER_DATE = "01-Jan-2500";
	private EmployeesHistoryTableModel tableModel;

	public EmployeesHistoryTable() {
		super();

		tableModel = new EmployeesHistoryTableModel();
		setModel(tableModel);

		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tableModel);
		sorter.setComparator(0, new Comparator<Date>() {
			@Override
			public int compare(Date o1, Date o2) {
				return o1.compareTo(o2);
			}
		});
		this.setRowSorter(sorter);
	}

	public void addEmployeeModel(EmployeeModel employeeModel) {
		tableModel.addEmployeeModel(employeeModel);
	}

	public void setColumnsWidth() {
		for (int i = 0; i<= 4; i++){
			switch (i){
				case 0:
					getColumnModel().getColumn(i).setPreferredWidth(80);
					getColumnModel().getColumn(i).setMaxWidth(80);
					break;
				case 1:
				case 2:
					getColumnModel().getColumn(i).setPreferredWidth(80);
					getColumnModel().getColumn(i).setMaxWidth(80);
					break;
				case 3:
					getColumnModel().getColumn(i).setPreferredWidth(80);
					break;
				case 4:
					getColumnModel().getColumn(i).setPreferredWidth(120);
					break;
				default: getColumnModel().getColumn(i).setPreferredWidth(80);
			}
		}
	}

	private class EmployeesHistoryTableModel extends AbstractTableModel {
		private String[] columnNames;
		private ArrayList<EmployeeModel> objectsList;

		public EmployeesHistoryTableModel() {
			columnNames = new String[] {"FROM", "TO", "NAME", "SURNAME", "LOCATION"};
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

			if (employeeModel == null) return (Object)"";

			switch (columnIndex) {
				case 0:
					return employeeModel.getDateFrom();
				case 1:
					if (employeeModel.getDateTo() != null)
						return getStringDate(employeeModel.getDateTo());
				case 2:
					return employeeModel.getName();
				case 3:
					return employeeModel.getSurname();
				case 4:
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
			if (Utils.removeTime(dateTo).equals(foreverDate)){
				return "now";
			}
			return dateTo.toString();
		}

		@Override
		public String getColumnName(int columnIndex) {
			if(columnIndex >= columnNames.length)
				return "";

			return columnNames[columnIndex];
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}
	}

}
