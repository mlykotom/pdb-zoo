package cz.vutbr.fit.pdb.ateam.controller;

import cz.vutbr.fit.pdb.ateam.adapter.DataManager;
import cz.vutbr.fit.pdb.ateam.exception.DataManagerException;
import cz.vutbr.fit.pdb.ateam.gui.tabs.SpatialObjectsTab;
import cz.vutbr.fit.pdb.ateam.gui.tabs.lists.SpatialObjectsList;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialPointModel;
import cz.vutbr.fit.pdb.ateam.observer.ISpatialObjectSelectionChangedListener;
import cz.vutbr.fit.pdb.ateam.utils.Logger;
import cz.vutbr.fit.pdb.ateam.utils.Utils;
import cz.vutbr.fit.pdb.ateam.observer.SpatialObjectSelectionChangeObservable;


import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jakub on 24.10.2015.
 */
public class SpatialObjectTabController extends Controller implements ISpatialObjectSelectionChangedListener {
	SpatialObjectsTab spatialObjectsTab;
	SpatialObjectsList spatialObjectList;

	public SpatialObjectTabController(SpatialObjectsTab spatialObjectsTab) {
		super();
		this.spatialObjectsTab = spatialObjectsTab;
		this.spatialObjectList = new SpatialObjectsList(this);
		// TODO: objectDetail

		Utils.changePanelContent(spatialObjectsTab, spatialObjectList);

		fillUpSpatialObjectsTable();

		SpatialObjectSelectionChangeObservable.getInstance().subscribe(this);
	}

	private void fillUpSpatialObjectsTable() {
		SpatialObjectsTableModel tableModel = new SpatialObjectsTableModel();
		List<SpatialObjectModel> models;

		try {
			models = DataManager.getInstance().getAllSpatialObjects();
		} catch (DataManagerException e) {
			Logger.createLog(Logger.ERROR_LOG, e.getMessage());
			return;
		}

		for(SpatialObjectModel model : models) {
			tableModel.addSpatialObjectModel(model);
		}

		JTable table = new JTable(tableModel);

		spatialObjectList.setSpatialObjectsTable(table);
	}

	class ButtonRenderer extends JButton implements TableCellRenderer {

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

	class ButtonEditor extends DefaultCellEditor {
		protected JButton button;

		private String label;

		private boolean isPushed;

		public ButtonEditor(JCheckBox checkBox) {
			super(checkBox);
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
			return button;
		}

		public Object getCellEditorValue() {
			if (isPushed) {
				//
				//
				JOptionPane.showMessageDialog(button, label + ": Ouch!");
				// System.out.println(label + ": Ouch!");
			}
			isPushed = false;
			return new String(label);
		}

		public boolean stopCellEditing() {
			isPushed = false;
			return super.stopCellEditing();
		}

		protected void fireEditingStopped() {
			super.fireEditingStopped();
		}
	}


	public class SpatialObjectsTableModel extends AbstractTableModel {
		private String[] columnNames;
		private ArrayList<SpatialObjectModel> objectsList;

		public SpatialObjectsTableModel() {
			columnNames = new String[] {"ID", "NAME", "TYPE", ""};
			objectsList = new ArrayList<>();
		}

		public void addSpatialObjectModel(SpatialObjectModel spatialObjectModel) {
			objectsList.add(spatialObjectModel);
			fireTableRowsInserted(objectsList.size() - 1, objectsList.size() - 1);
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
	}

	/**
	 * Fires when spatial object is selected on zoo map canvas.
	 *
	 * @param spatialObjectModel selected spatial object model
	 */
	@Override
	public void spatialObjectSelectionChangedListener(SpatialObjectModel spatialObjectModel) {
		System.out.println("PRD");
	}
}
