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
import java.util.Vector;

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

		createTable();

		SpatialObjectSelectionChangeObservable.getInstance().subscribe(this);
	}

	private void createTable() {
		String [] columnNames = {
				"ID", "NAME", "TYPE", ""
		};

		JTable table = new JTable(new SpatialObjectsTableModel(columnNames));

		try {
			SpatialObjectModel model = DataManager.getInstance().getAllSpatialObjects().get(0);
		} catch (DataManagerException e) {
			e.printStackTrace();
		}

		Utils.changePanelContent(spatialObjectList, table);
	}

	public class SpatialObjectsTableModel extends AbstractTableModel {
		public static final int ID_INDEX = 0;
		public static final int NAME_INDEX = 1;
		public static final int TYPE_INDEX = 2;

		protected String[] columnNames;
		protected Vector dataVector;

		public SpatialObjectsTableModel(String[] columnNames) {
			this.columnNames = columnNames;
			dataVector = new Vector();
		}

		public String getColumnName(int column) {
			return columnNames[column];
		}

		public boolean isCellEditable(int row, int column) {
			return false;
		}

		public Class getColumnClass(int column) {
			switch (column) {
				case ID_INDEX:
					return Long.class;
				case NAME_INDEX:
				case TYPE_INDEX:
					return String.class;
				default:
					return Object.class;
			}
		}

		public Object getValueAt(int row, int column) {
			SpatialObjectModel spatialObjectModel = (SpatialObjectModel)dataVector.get(row);
			switch (column) {
				case ID_INDEX:
					spatialObjectModel.getId();
				case NAME_INDEX:
					return spatialObjectModel.getName();
				case TYPE_INDEX:
					return spatialObjectModel.getType().getName();
				default:
					return new Object();
			}
		}

		public void setValueAt(Object value, int row, int column) {
			SpatialObjectModel spatialObjectModel = (SpatialObjectModel)dataVector.get(row);
			switch (column) {
				case ID_INDEX:
					return;
				case NAME_INDEX:
					spatialObjectModel.setName((String) value);
					break;
				case TYPE_INDEX:
					spatialObjectModel.getType().setName((String) value);
					break;
				default:
					Logger.createLog(Logger.ERROR_LOG, "Out of index!");
			}
			fireTableCellUpdated(row, column);
		}

		public int getRowCount() {
			return dataVector.size();
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public boolean hasEmptyRow() {
			return false;
		}

		public void addEmptyRow() {
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
