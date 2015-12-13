package cz.vutbr.fit.pdb.ateam.gui.components;

import cz.vutbr.fit.pdb.ateam.adapter.DataManager;
import cz.vutbr.fit.pdb.ateam.model.animal.AnimalModel;
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
 * Created by Tomas on 12/12/2015.
 */
public class AnimalDetailTable extends JTable {
	private static final String FOREVER_DATE = "01-Jan-2500";
	private AnimalDetailTableModel tableModel;

	public AnimalDetailTable() {
		super();

		tableModel = new AnimalDetailTableModel();
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

	public void addAnimalModel(AnimalModel animalModel) {
		tableModel.addAnimalModel(animalModel);
	}

	public void setColumnsWidth() {
		for (int i = 0; i<= 4; i++){
			switch (i){
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
				case 4:
					getColumnModel().getColumn(i).setPreferredWidth(70);
					getColumnModel().getColumn(i).setMaxWidth(70);
					break;
				default: getColumnModel().getColumn(i).setPreferredWidth(80);
			}
		}
	}

	private class AnimalDetailTableModel extends AbstractTableModel {
		private String[] columnNames;
		private ArrayList<AnimalModel> objectsList;

		public AnimalDetailTableModel() {
			columnNames = new String[] {"ID", "FROM", "TO", "LOCATION", "WEIGHT"};
			objectsList = new ArrayList<>();
		}

		public void addAnimalModel(AnimalModel animalModel) {
			objectsList.add(animalModel);
			fireTableRowsInserted(objectsList.size() - 1, objectsList.size() - 1);
		}


		public AnimalModel getAnimalModel(int rowIndex) {
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
			AnimalModel animalModel = objectsList.get(rowIndex);

			if (animalModel == null) return (Object)"";

			switch (columnIndex) {
				case 0:
					return animalModel.getShiftID();
				case 1:
					return animalModel.getDateFrom();
				case 2:
					if (animalModel.getDateTo() != null)
						return getStringDate(animalModel.getDateTo());
				case 3:
					if (DataManager.getInstance().getSpatialObjectModelWithID(animalModel.getLocation()) != null)
						return DataManager.getInstance().getSpatialObjectModelWithID(animalModel.getLocation()).toString();
				case 4:
						return animalModel.getWeight();
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
			return columnIndex == 4 || columnIndex == 5;
		}
	}
}
