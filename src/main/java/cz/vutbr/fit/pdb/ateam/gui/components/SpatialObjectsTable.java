package cz.vutbr.fit.pdb.ateam.gui.components;

import cz.vutbr.fit.pdb.ateam.controller.SpatialObjectTableController;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Jakub on 25.10.2015.
 */
public class SpatialObjectsTable extends JTable {
	private SpatialObjectsTableModel tableModel;

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

	public void addSpatialObjectModel(SpatialObjectModel spatialObjectModel) {
		tableModel.addSpatialObjectModel(spatialObjectModel);
	}

	private class SpatialObjectsTableModel extends AbstractTableModel {
		private String[] columnNames;
		private ArrayList<SpatialObjectModel> objectsList;

		public SpatialObjectsTableModel() {
			columnNames = new String[] {"ID", "NAME", "TYPE", "DELETE", "EDIT"};
			objectsList = new ArrayList<>();
		}

		public void addSpatialObjectModel(SpatialObjectModel spatialObjectModel) {
			objectsList.add(spatialObjectModel);
			fireTableRowsInserted(objectsList.size() - 1, objectsList.size() - 1);
		}


		public SpatialObjectModel getSpatialObjectModel(int rowIndex) {
			return objectsList.get(getRowSorter().convertRowIndexToModel(rowIndex));
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

		@Override
		public String getColumnName(int columnIndex) {
			if(columnIndex >= columnNames.length)
				return "";

			return columnNames[columnIndex];
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == 3 || columnIndex == 4;
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
