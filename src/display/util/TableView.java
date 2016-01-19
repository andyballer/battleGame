package display.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import display.Constant;

/**
 * a generic view for list display
 */

@SuppressWarnings("serial")
public class TableView extends Panel {
	
	protected String label;
	protected JTable tableBox;
	protected DefaultTableModel tableModel;
	
	protected JButton button = null;
	
	// indicate whether the current selections are valid to trigger the listselection listener
	protected boolean selectedValid = false;
	
	public TableView(String _label, EmptyBorder border, Dimension dim, boolean isMultiSelection, boolean isToggle, boolean hasButton) {
		label = _label;

		masterPanel = new JPanel();
		masterPanel.setLayout(new BorderLayout());
		masterPanel.setBorder(border);
		masterPanel.setPreferredSize(dim);
		
		JLabel info = new JLabel(label);
		info.setFont(Constant.titleFont);
		
		if (hasButton) {
			JPanel p = new JPanel();
			p.setLayout(new BorderLayout());
			p.add(info, BorderLayout.WEST);
			
			button = new JButton(Constant.exitTypeTag);
			p.add(button, BorderLayout.EAST);
			
			masterPanel.add(p, BorderLayout.NORTH);
		} else
			masterPanel.add(info, BorderLayout.NORTH);
			
		if (isToggle)
			tableBox = new JTable() {
				public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
					super.changeSelection(rowIndex, columnIndex, !extend, extend);
				}
			};
		else
			tableBox = new JTable();
		   
        tableBox.setFillsViewportHeight(true);
        tableBox.setFont(Constant.contentFont);
        
        if (!isMultiSelection)
        	tableBox.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(tableBox);		
		masterPanel.add(scrollPane);
	}
	
	public void setTableModel(AbstractTableModel m) {
		tableBox.setModel(m);
	}
	
	public void setListSelectionListener(ListSelectionListener listener) {
		tableBox.getSelectionModel().addListSelectionListener(listener);
	}
	
	public void clearSelections() {
		tableBox.clearSelection();
	}
	
	public void setButtonActionListener(ActionListener action) {
		if (button != null)
			button.addActionListener(action);
	}
	
	public void setListSelectionValid(boolean b) {
		selectedValid = b;
	}
	
	public boolean getListSelectionValid() {
		return selectedValid;
	}

}
