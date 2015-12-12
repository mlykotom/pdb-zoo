package cz.vutbr.fit.pdb.ateam.gui.components;

import cz.vutbr.fit.pdb.ateam.controller.SpatialObjectTableController;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.utils.Utils;

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
 * Table for the spatial objects. Includes columns saved in the database and
 * delete, edit buttons.
 *
 * @author Jakub Tutko
 */
public class SpatialObjectsTable extends JTable {
	private SpatialObjectsTableModel tableModel;

	/**
	 * Constructor creates empty table.
	 *
	 * @param controller controller which implements delete & edit actions of the SpatialObjectsTable
	 */
	public SpatialObjectsTable (SpatialObjectTableController controller) {
		super();

		tableModel = new SpatialObjectsTableModel();

		setModel(tableModel);

		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tableModel);
		sorter.setComparator(0, new Comparator<Long>() {
			@Override
			public int compare(Long o1, Long o2) {
				return o1.compareTo(o2);
			}
		});
		this.setRowSorter(sorter);

		getColumn("DELETE").setCellRenderer(new ButtonRenderer());
		getColumn("DELETE").setCellEditor(
				new ButtonEditor(controller));

		getColumn("EDIT").setCellRenderer(new ButtonRenderer());
		getColumn("EDIT").setCellEditor(
				new ButtonEditor(controller));
	}

	/**
	 * Method adds object as new row into table.
	 *
	 * @param spatialObjectModel model of the spatial object
	 */
	public void addSpatialObjectModel(SpatialObjectModel spatialObjectModel) {
		tableModel.addSpatialObjectModel(spatialObjectModel);
	}

	/**
	 * Table model for the spatial object table.
	 *
	 * @author Jakub Tutko
	 */
	private class SpatialObjectsTableModel extends AbstractTableModel {
		private String[] columnNames;
		private ArrayList<SpatialObjectModel> objectsList;

		/**
		 * Constructor creates columns in the table (ID, NAME, TYPE, DELETE, EDIT).
		 */
		public SpatialObjectsTableModel() {
			columnNames = new String[] {"ID", "NAME", "TYPE", "DELETE", "EDIT"};
			objectsList = new ArrayList<>();
		}

		/**
		 * Ads model as row into the table.
		 *
		 * @param spatialObjectModel model to insert into table
		 */
		public void addSpatialObjectModel(SpatialObjectModel spatialObjectModel) {
			objectsList.add(spatialObjectModel);
			fireTableRowsInserted(objectsList.size() - 1, objectsList.size() - 1);
		}

		/**
		 * Returns spatial object at the selected row.
		 *
		 * @param rowIndex index of the row
		 * @return spatial object at the selected row
		 */
		public SpatialObjectModel getSpatialObjectModel(int rowIndex) {
			return objectsList.get(getRowSorter().convertRowIndexToModel(rowIndex));
		}

		/**
		 * Counts table's rows.
		 *
		 * @return table's rows count
		 */
		@Override
		public int getRowCount() {
			return objectsList.size();
		}

		/**
		 * Counts table's columns.
		 *
		 * @return table's columns count
		 */
		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		/**
		 * Returns object value according to column and row index.
		 *
		 * @param rowIndex row index of the value
		 * @param columnIndex column index of the value
		 * @return value at selected row & column
		 */
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			SpatialObjectModel spatialObjectModel = objectsList.get(rowIndex);

			switch (columnIndex) {
				case 0:
					return spatialObjectModel.getId();
				case 1:
					return spatialObjectModel.getName();
				case 2:
					return spatialObjectModel.getType().getName();
				case 3:
					return "Delete";
				case 4:
					return "Edit";
				default:
					return "";
			}
		}

		/**
		 * Returns name of the selected column.
		 *
		 * @param columnIndex index of the column
		 * @return name of the selected column
		 */
		@Override
		public String getColumnName(int columnIndex) {
			if(columnIndex >= columnNames.length)
				return "";

			return columnNames[columnIndex];
		}

		/**
		 * Tells if specified cell is editable.
		 *
		 * @param rowIndex index of the row
		 * @param columnIndex index of the column
		 * @return false if cell contains button, true otherwise
		 */
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == 3 || columnIndex == 4;
		}
	}

	/**
	 * Class renders button in the table
	 *
	 * @author Jakub Tutko
	 */
	private class ButtonRenderer extends JButton implements TableCellRenderer {

		public ButtonRenderer() {
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
		                                               boolean isSelected, boolean hasFocus, int row, int column) {

			Utils.setButtonForegroundAndBackground(isSelected, this, table);
			setText((value == null) ? "" : value.toString());
			return this;
		}
	}

	/**
	 * Class edits buttons in the table.
	 *
	 * @author Jakub Tutko
	 */
	private class ButtonEditor extends DefaultCellEditor {
		protected JButton button;

		private String label;

		private SpatialObjectTableController controller;
		private SpatialObjectModel spatialObjectModel;
		private String pushedButton;

		private boolean isPushed;

		public ButtonEditor(SpatialObjectTableController controller) {
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

			Utils.setButtonForegroundAndBackground(isSelected, button, table);
			label = (value == null) ? "" : value.toString();
			button.setText(label);
			isPushed = true;

			this.spatialObjectModel = ((SpatialObjectsTableModel)table.getModel()).getSpatialObjectModel(row);
			if(column == 3) this.pushedButton = "delete";
			if(column == 4) this.pushedButton = "edit";

			return button;
		}

		public Object getCellEditorValue() {
			if (isPushed) {
				if(pushedButton.equals("delete")) controller.spatialObjectsTableDeleteAction(spatialObjectModel);
				if(pushedButton.equals("edit")) controller.spatialObjectsTableEditAction(spatialObjectModel);
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
