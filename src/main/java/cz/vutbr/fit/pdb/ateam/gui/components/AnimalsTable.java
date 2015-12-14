package cz.vutbr.fit.pdb.ateam.gui.components;

import cz.vutbr.fit.pdb.ateam.adapter.DataManager;
import cz.vutbr.fit.pdb.ateam.controller.AnimalsTableController;
import cz.vutbr.fit.pdb.ateam.model.animal.AnimalModel;
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
 * @Author by Tomas Hanus on 12/12/2015.
 */
public class AnimalsTable extends JTable {
	private AnimalsTableModel tableModel;

	public AnimalsTable(AnimalsTableController controller) {
		super();

		tableModel = new AnimalsTableModel();

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

	public void addAnimalModel(AnimalModel animalModel) {
		tableModel.addAnimalModel(animalModel);
	}

	public void setColumnsWidth() {
		for (int i = 0; i <= 4; i++) {
			switch (i) {
				case 0:
					getColumnModel().getColumn(i).setPreferredWidth(35);
					getColumnModel().getColumn(i).setMaxWidth(35);
					break;
				case 1:
				case 2:
					getColumnModel().getColumn(i).setPreferredWidth(70);
					break;
				case 3:
					getColumnModel().getColumn(i).setPreferredWidth(150);
					break;
				case 4:
					getColumnModel().getColumn(i).setPreferredWidth(55);
					getColumnModel().getColumn(i).setMaxWidth(55);
					break;

				default:
					getColumnModel().getColumn(i).setPreferredWidth(80);
			}
		}
	}

	private class AnimalsTableModel extends AbstractTableModel {
		private String[] columnNames;
		private ArrayList<AnimalModel> objectsList;

		public AnimalsTableModel() {
			columnNames = new String[]{"ID", "NAME", "SPECIES", "LOCATION", "WEIGHT", "EDIT"};
			objectsList = new ArrayList<>();
		}

		public void addAnimalModel(AnimalModel animalModel) {
			objectsList.add(animalModel);
			fireTableRowsInserted(objectsList.size() - 1, objectsList.size() - 1);
		}


		public AnimalModel getAnimalModel(int rowIndex) {
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
			AnimalModel animalModel = objectsList.get(rowIndex);

			switch (columnIndex) {
				case 0:
					return animalModel.getId();
				case 1:
					return animalModel.getName();
				case 2:
					return animalModel.getSpecies();
				case 3:
					return DataManager.getInstance().getSpatialObjectModelWithID(animalModel.getLocation()).toString();
				case 4:
					return animalModel.getWeight();
				case 5:
					return "Edit";
				default:
					return "";
			}
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

		private AnimalsTableController controller;
		private AnimalModel animalModel;
		private String pushedButton;

		private boolean isPushed;

		public ButtonEditor(AnimalsTableController controller) {
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

			this.animalModel = ((AnimalsTableModel) table.getModel()).getAnimalModel(row);
			if (column == 5) this.pushedButton = "edit";

			return button;
		}

		public Object getCellEditorValue() {
			if (isPushed) {
				if (pushedButton.equals("edit")) controller.AnimalsTableEditAction(animalModel);
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
