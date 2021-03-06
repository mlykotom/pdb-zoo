package cz.vutbr.fit.pdb.ateam.gui.components;

import cz.vutbr.fit.pdb.ateam.gui.BasePanel;
import cz.vutbr.fit.pdb.ateam.model.BaseModel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Tomas Mlynaric
 */
public class SpatialDetailTable extends JTable {

	private BaseTableModel tableModel;
	private List<Column> columns = new ArrayList<>();
	public SpatialDetailTable() {
		super();

		Collections.addAll(columns,
				new Column("ID", 50),
				new Column("NAME", 250),
				new Column("DISTANCE [m]", 150)
		);

		tableModel = new BaseTableModel(columns);
		setModel(tableModel);

		setColumnsWidth();
		setSorter();
	}

	/**
	 * Helper for setting up sorting in table
	 */
	private void setSorter() {
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tableModel);
		sorter.setComparator(0, new Comparator<Long>() {
			@Override
			public int compare(Long o1, Long o2) {
				return o1.compareTo(o2);
			}
		});
		this.setRowSorter(sorter);
	}

	public void addModel(BaseModel model) {
		tableModel.addModel(model);
	}

	public void setModels(List<? extends BaseModel> models) {
		tableModel.setModels(models);
	}

	public void clearModels() {
		tableModel.clearModels();
	}

	public void setColumnsWidth() {
		int i = 0;
		for (Column column : columns) {
			getColumnModel().getColumn(i).setPreferredWidth(column.getPreferredWidth());
			getColumnModel().getColumn(i).setMaxWidth(column.getPreferredWidth());
			i++;
		}
	}

	/**
	 * Class for column setup in table
	 */
	private class Column {
		private String name;
		private int preferredWidth;

		public Column(String name, int preferredWidth) {
			this.name = name;
			this.preferredWidth = preferredWidth;
		}

		public String getName() {
			return name;
		}

		public int getPreferredWidth() {
			return preferredWidth;
		}
	}

	private class BaseTableModel extends AbstractTableModel {
		private List<Column> columns;
		private ArrayList<BaseModel> objectsList;

		public BaseTableModel(List<Column> columnNames) {
			this.columns = columnNames;
			objectsList = new ArrayList<>();
		}

		public void addModel(BaseModel model) {
			objectsList.add(model);
			fireTableRowsInserted(objectsList.size() - 1, objectsList.size() - 1);
		}

		public void setModels(List<? extends BaseModel> models) {
			objectsList.clear();
			objectsList.addAll(models);
			fireTableDataChanged();
		}

		public BaseModel getModel(int rowIndex) {
			return objectsList.get(rowIndex);
		}

		@Override
		public int getRowCount() {
			return objectsList.size();
		}

		@Override
		public int getColumnCount() {
			return columns.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			BaseModel model = getModel(rowIndex);

			switch (columnIndex) {
				case 0:
					return model.getId();
				case 1:
					return model.getName();
				case 2:
					Object additional = model.getAdditionalInformations().get(SpatialObjectModel.ADDITIONAL_CLOSEST_DISTANCE);
					return additional == null ? "" : BasePanel.doubleFormatter.format(additional);
				default:
					return "";
			}
		}

		@Override
		public String getColumnName(int columnIndex) {
			if (columnIndex >= columns.size())
				return "";

			return columns.get(columnIndex).getName();
		}

		public void clearModels() {
			objectsList.clear();
			fireTableDataChanged();
		}
	}
}
