package display.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import display.Constant;
import engine.ResourceLoader;

public class ImageTableView extends TableView {

	public ImageTableView(String _label, EmptyBorder border, Dimension dim, boolean isMultiSelection, boolean isToggle, boolean hasButton) {
		super(_label, border, dim, isMultiSelection, isToggle, hasButton);
		tableModel = new ImageTableModel();
		tableBox.setModel(tableModel);
		tableModel.setColumnIdentifiers(Constant.identifiers);
		tableBox.getColumnModel().getColumn(1).setMaxWidth(80);
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		tableBox.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
	}

	public void addImages(String tag) {
		Map<String, String> picsToPaths = ResourceLoader.load(tag, Constant.imagePropertyFile);
		List<ImageLabelCell> cells = new ArrayList<ImageLabelCell>();
		
		for (String picName : picsToPaths.keySet()) {
			Constant.LOG(this.getClass(), "tag: " + picsToPaths.toString());
			Icon image = Layout.createImageIcon(picsToPaths.get(picName));
			JLabel label = new JLabel(picName, image, JLabel.LEFT);
			cells.add(new ImageLabelCell(picName, label));
		}
		((ImageTableModel)tableModel).updateData(cells);
		tableBox.setDefaultRenderer(ImageLabelCell.class, new ImageCellRenderer());
		
		tableBox.setRowHeight(64);
	}
	
	public void addImages(Map<String, String> nameToPaths) {
		List<ImageLabelCell> cells = new ArrayList<ImageLabelCell>();
		
		for (String picName : nameToPaths.keySet()) {
			Icon image = Layout.createImageIcon(nameToPaths.get(picName));
			JLabel label = new JLabel(picName, image, JLabel.LEFT);
			cells.add(new ImageLabelCell(picName, label));
		}
		((ImageTableModel)tableModel).updateData(cells);
		tableBox.setDefaultRenderer(ImageLabelCell.class, new ImageCellRenderer());
		
		tableBox.setRowHeight(64);
	}
	
	public void clearAll() {
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			tableModel.removeRow(0);
		}
		((ImageTableModel)tableModel).data.clear();
	}
	
	public Map<String, Integer> getSelections() {
		int[] selects = tableBox.getSelectedRows();
		Map<String, Integer> inventory = new HashMap<String, Integer>();
		for (int i : selects) {
			inventory.put(((ImageTableModel)tableModel).data.get(i).label.getText(), (Integer)tableModel.getValueAt(i, 1));
		}
		Constant.LOG(this.getClass(), "INV: " + inventory.toString());
		return inventory;
	}
	
	public void setSelections(Map<String, Integer> selects) {
		Set<String> keys = selects.keySet();
		tableBox.clearSelection();
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			String name = ((ImageTableModel)tableModel).data.get(i).label.getText();
			if (keys.contains(name)) {
				tableBox.addRowSelectionInterval(i, i);
				((ImageTableModel)tableModel).setValueAt(selects.get(name), i, 1);
			}
		}
		tableBox.setColumnSelectionInterval(0, 0);
	}
	
	private class ImageTableModel extends DefaultTableModel {

		private static final long serialVersionUID = -8898012837505526281L;
		List<ImageLabelCell> data = new ArrayList<ImageLabelCell>();
		
		private void updateData(List<ImageLabelCell> d) {
			data = d;
			for (int i = 0; i < data.size(); i++) {
				Constant.LOG(this.getClass(), data.size() + " " + data.get(i).toString());
				this.insertRow(i, new Object[]{data.get(i), 1});
			}
		}

		public Class<?> getColumnClass(int columnIndex) { 
			if (columnIndex == 0)
				return ImageLabelCell.class; 
			else 
				return Integer.class;
		}
		
		public int getRowCount() { return (data == null ? 0 : data.size()); }
		public int getColumnCount() { return Constant.identifiers.length; }
		public String getColumnName(int columnIndex) { 
			return (String)Constant.identifiers[columnIndex]; 
		}
	
		public boolean isCellEditable(int columnIndex, int rowIndex) { 
			return true;
		}
	}
	
	private class ImageLabelCell {
		private JLabel label;
		private String name;
		
		private ImageLabelCell(String n, JLabel l) {
			name = n;
			label = l;
		}
		
		public String toString() {
			return label + " " + name;
		}
	}

	private class ImageCellRenderer implements TableCellRenderer {

		public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column ) {
			ImageLabelCell cell = (ImageLabelCell)value;

			if (cell != null) {
				JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				panel.add(cell.label);
				panel.setName(cell.name);
				panel.setFont(Constant.contentFont);
				if (isSelected) {
					panel.setBackground(UIManager.getColor("Table.focusCellForeground"));
				}else{
					panel.setBackground(Color.white);
				}
				return panel;
			}
			return null;

		}
	}
}

